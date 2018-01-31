/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.webhook.ws;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.setting.ws.Setting;
import org.sonar.server.setting.ws.SettingsFinder;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.Webhooks.SearchWsResponse.Builder;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.sonar.api.web.UserRole.ADMIN;
import static org.sonar.server.webhook.ws.WebhooksWsParameters.ORGANIZATION_KEY_PARAM;
import static org.sonar.server.webhook.ws.WebhooksWsParameters.PROJECT_KEY_PARAM;
import static org.sonar.server.webhook.ws.WebhooksWsParameters.SEARCH_ACTION;
import static org.sonar.server.ws.KeyExamples.KEY_ORG_EXAMPLE_001;
import static org.sonar.server.ws.KeyExamples.KEY_PROJECT_EXAMPLE_001;
import static org.sonar.server.ws.WsUtils.writeProtobuf;
import static org.sonarqube.ws.Webhooks.SearchWsResponse.newBuilder;

public class SearchAction implements WebhooksWsAction {

  private final DbClient dbClient;
  private final UserSession userSession;
  private final SettingsFinder settingsFinder;

  public SearchAction(DbClient dbClient, UserSession userSession, SettingsFinder settingsFinder) {
    this.dbClient = dbClient;
    this.userSession = userSession;
    this.settingsFinder = settingsFinder;
  }

  @Override
  public void define(WebService.NewController controller) {

    WebService.NewAction action = controller.createAction(SEARCH_ACTION)
      .setDescription("Search for webhooks associated to an organization or a project.<br/>")
      .setSince("7.1")
      .setResponseExample(Resources.getResource(this.getClass(), "example-webhooks-search.json"))
      .setHandler(this);

    action.createParam(ORGANIZATION_KEY_PARAM)
      .setDescription("Organization key. If no organization is provided, the default organization is used.")
      .setInternal(true)
      .setRequired(false)
      .setExampleValue(KEY_ORG_EXAMPLE_001);

    action.createParam(PROJECT_KEY_PARAM)
      .setDescription("Project key")
      .setRequired(false)
      .setExampleValue(KEY_PROJECT_EXAMPLE_001);

  }

  @Override
  public void handle(Request request, Response response) throws Exception {

    String project = request.param(PROJECT_KEY_PARAM);

    userSession.checkLoggedIn();

    List<SearchElement> elements = doHandle(project);

    writeResponse(request, response, elements);

  }

  private List<SearchElement> doHandle(String project) {

    try (DbSession dbSession = dbClient.openSession(true)) {

      List<Setting> settings = new ArrayList<>();

      if (isNotBlank(project)) {
        Optional<ComponentDto> component = dbClient.componentDao().selectByKey(dbSession, project);
        if (component.isPresent()) {
          userSession.checkComponentPermission(ADMIN, component.get());
          settings = settingsFinder.loadProjectSettings(dbSession, component.get(), "sonar.webhooks.project");
        }
      } else {
        userSession.checkIsSystemAdministrator();
        settings = settingsFinder.loadGlobalSettings(dbSession, ImmutableSet.of("sonar.webhooks.global"));
      }

      return settings
        .stream()
        .map(Setting::getPropertySets)
        .flatMap(Collection::stream)
        .map(map -> new SearchElement("", map.get("name"), map.get("url")))
        .collect(toList());

    }
  }

  private static void writeResponse(Request request, Response response, List<SearchElement> searchElements) {
    Builder searchResponse = newBuilder();
    for (SearchElement element : searchElements) {
      searchResponse.addWebhooksBuilder()
        .setKey(element.getKey())
        .setName(element.getName())
        .setUrl(element.getUrl());
    }

    writeProtobuf(searchResponse.build(), request, response);
  }

}
