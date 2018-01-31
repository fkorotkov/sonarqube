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
import { getJSON, post } from '../helpers/request';
import { BranchLike, PullRequest } from '../app/types';
import throwGlobalError from '../app/utils/throwGlobalError';

const samplePullRequest: PullRequest = {
  analysisDate: '2017-01-02T00:00:00.000Z',
  base: 'master',
  branch: 'feature/stas/pr-api',
  id: '2734',
  name: 'SONAR-10374 Support pull request in the web app',
  status: { bugs: 1, codeSmells: 3, vulnerabilities: 0 }
};

const samplePullRequest2: PullRequest = {
  analysisDate: '2017-01-02T00:00:00.000Z',
  base: 'branch-6.7',
  branch: 'feature/stas/my-bug-fix',
  id: '2725',
  name: 'fix critical LTS issue',
  status: { bugs: 0, codeSmells: 0, vulnerabilities: 0 }
};

const orphanPullRequest: PullRequest = {
  analysisDate: '2017-01-02T00:00:00.000Z',
  base: 'unknown-branch',
  branch: 'feature/stas/unknown-branch',
  id: '9999',
  name: 'create orphan pull request',
  status: { bugs: 0, codeSmells: 0, vulnerabilities: 0 }
};

export function getBranches(project: string): Promise<BranchLike[]> {
  return getJSON('/api/project_branches/list', { project }).then(
    r => [...r.branches, samplePullRequest, samplePullRequest2, orphanPullRequest] as any,
    throwGlobalError
  );
}

export function deleteBranch(data: { project: string; branch?: string; pullRequest?: string }) {
  return post('/api/project_branches/delete', data).catch(throwGlobalError);
}

export function renameBranch(project: string, name: string) {
  return post('/api/project_branches/rename', { project, name }).catch(throwGlobalError);
}
