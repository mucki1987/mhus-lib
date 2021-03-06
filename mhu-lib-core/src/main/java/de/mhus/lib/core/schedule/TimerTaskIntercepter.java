/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.schedule;

import de.mhus.lib.core.strategy.DefaultTaskContext;

public interface TimerTaskIntercepter {

	void initialize(SchedulerJob job);
	boolean beforeExecution(SchedulerJob job, DefaultTaskContext context, boolean forced);
	void afterExecution(SchedulerJob job, DefaultTaskContext context);
	void onError(SchedulerJob schedulerJob, DefaultTaskContext context, Throwable e);
}
