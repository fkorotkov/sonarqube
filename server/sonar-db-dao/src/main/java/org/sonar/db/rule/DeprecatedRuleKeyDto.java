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

package org.sonar.db.rule;

public class DeprecatedRuleKeyDto {
  private String uuid;
  private Integer ruleId;
  private String oldRepositoryKey;
  private String oldRuleKey;
  private Long createdAt;
  private String newRepositoryKey;
  private String newRuleKey;

  public String getUuid() {
    return uuid;
  }

  public DeprecatedRuleKeyDto setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public Integer getRuleId() {
    return ruleId;
  }

  public DeprecatedRuleKeyDto setRuleId(Integer ruleId) {
    this.ruleId = ruleId;
    return this;
  }

  public String getOldRepositoryKey() {
    return oldRepositoryKey;
  }

  public DeprecatedRuleKeyDto setOldRepositoryKey(String oldRepositoryKey) {
    this.oldRepositoryKey = oldRepositoryKey;
    return this;
  }

  public String getOldRuleKey() {
    return oldRuleKey;
  }

  public DeprecatedRuleKeyDto setOldRuleKey(String oldRuleKey) {
    this.oldRuleKey = oldRuleKey;
    return this;
  }

  public String getNewRepositoryKey() {
    return newRepositoryKey;
  }

  public DeprecatedRuleKeyDto setNewRepositoryKey(String newRepositoryKey) {
    this.newRepositoryKey = newRepositoryKey;
    return this;
  }

  public String getNewRuleKey() {
    return newRuleKey;
  }

  public DeprecatedRuleKeyDto setNewRuleKey(String newRuleKey) {
    this.newRuleKey = newRuleKey;
    return this;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public DeprecatedRuleKeyDto setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }
}
