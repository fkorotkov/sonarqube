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
import { getJSON, RequestData } from '../helpers/request';
import throwGlobalError from '../app/utils/throwGlobalError';
import { Measure, MeasurePeriod } from '../helpers/measures';
import { Metric } from '../app/types';
import { Period } from '../helpers/periods';

export function getMeasures(data: {
  componentKey: string;
  metricKeys: string;
  branch?: string;
  pullRequest?: string;
}): Promise<{ metric: string; value?: string }[]> {
  return getJSON('/api/measures/component', data).then(r => r.component.measures, throwGlobalError);
}

interface MeasureComponent {
  key: string;
  description?: string;
  measures: Measure[];
  name: string;
  qualifier: string;
}

export function getMeasuresAndMeta(
  componentKey: string,
  metrics: string[],
  additional: RequestData = {}
): Promise<{ component: MeasureComponent; metrics?: Metric[]; periods?: Period[] }> {
  const data = { ...additional, componentKey, metricKeys: metrics.join(',') };
  return getJSON('/api/measures/component', data);
}

interface MeasuresForProjects {
  component: string;
  metric: string;
  periods?: MeasurePeriod[];
  value?: string;
}

export function getMeasuresForProjects(
  projectKeys: string[],
  metricKeys: string[]
): Promise<MeasuresForProjects[]> {
  return getJSON('/api/measures/search', {
    projectKeys: projectKeys.join(),
    metricKeys: metricKeys.join()
  }).then(r => r.measures);
}
