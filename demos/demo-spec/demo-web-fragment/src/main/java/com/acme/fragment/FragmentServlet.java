//
// ========================================================================
// Copyright (c) 1995-2022 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package com.acme.fragment;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FragmentServlet
 *
 * A web fragment jar.
 */

public class FragmentServlet extends HttpServlet
{
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        this.config = config;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            response.setContentType("text/html");
            ServletOutputStream out = response.getOutputStream();
            out.println("<html>");
            out.println("<h1>Jetty Fragment Servlet</h1>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }
}
