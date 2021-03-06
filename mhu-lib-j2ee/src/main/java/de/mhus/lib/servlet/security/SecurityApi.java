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
package de.mhus.lib.servlet.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.annotations.activator.DefaultImplementation;

@DefaultImplementation(DefaultSecurityWatch.class)
public interface SecurityApi {

	/**
	 * Checks a http request and decide if the request can be processed or not
	 * @param req
	 * @param res
	 * @return true if the request can be processed
	 */
	boolean checkHttpRequest(HttpServletRequest req, HttpServletResponse res);
	
}
