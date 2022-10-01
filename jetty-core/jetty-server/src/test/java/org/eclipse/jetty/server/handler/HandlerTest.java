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

package org.eclipse.jetty.server.handler;

import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpChannelTest;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test checks the mechanism of combining Handlers into a tree, but doesn't check their operation.
 * @see HttpChannelTest for testing of calling Handlers
 */
public class HandlerTest
{
    @Test
    public void testWrapperSetServer()
    {
        Server s = new Server();
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();
        a.setHandler(b);
        b.setHandler(c);

        a.setServer(s);
        assertThat(b.getServer(), equalTo(s));
        assertThat(c.getServer(), equalTo(s));
    }

    @Test
    public void testWrapperServerSet()
    {
        Server s = new Server();
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();
        a.setServer(s);
        b.setHandler(c);
        a.setHandler(b);

        assertThat(b.getServer(), equalTo(s));
        assertThat(c.getServer(), equalTo(s));
    }

    @Test
    public void testWrapperThisLoop()
    {
        Handler.Wrapper a = new Handler.Wrapper();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> a.setHandler(a));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testWrapperSimpleLoop()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();

        a.setHandler(b);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> b.setHandler(a));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testWrapperDeepLoop()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();

        a.setHandler(b);
        b.setHandler(c);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> c.setHandler(a));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testWrapperChainLoop()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();

        a.setHandler(b);
        c.setHandler(a);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> b.setHandler(c));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testHandlerCollectionSetServer()
    {
        Server s = new Server();
        Handler.Collection a = new Handler.Collection();
        Handler.Collection b = new Handler.Collection();
        Handler.Collection b1 = new Handler.Collection();
        Handler.Collection b2 = new Handler.Collection();
        Handler.Collection c = new Handler.Collection();
        Handler.Collection c1 = new Handler.Collection();
        Handler.Collection c2 = new Handler.Collection();

        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(b1, b2);
        c.setHandlers(c1, c2);
        a.setServer(s);

        assertThat(b.getServer(), equalTo(s));
        assertThat(c.getServer(), equalTo(s));
        assertThat(b1.getServer(), equalTo(s));
        assertThat(b2.getServer(), equalTo(s));
        assertThat(c1.getServer(), equalTo(s));
        assertThat(c2.getServer(), equalTo(s));
    }

    @Test
    public void testHandlerCollectionServerSet()
    {
        Server s = new Server();
        Handler.Collection a = new Handler.Collection();
        Handler.Collection b = new Handler.Collection();
        Handler.Collection b1 = new Handler.Collection();
        Handler.Collection b2 = new Handler.Collection();
        Handler.Collection c = new Handler.Collection();
        Handler.Collection c1 = new Handler.Collection();
        Handler.Collection c2 = new Handler.Collection();

        a.setServer(s);
        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(b1, b2);
        c.setHandlers(c1, c2);

        assertThat(b.getServer(), equalTo(s));
        assertThat(c.getServer(), equalTo(s));
        assertThat(b1.getServer(), equalTo(s));
        assertThat(b2.getServer(), equalTo(s));
        assertThat(c1.getServer(), equalTo(s));
        assertThat(c2.getServer(), equalTo(s));
    }

    @Test
    public void testHandlerCollectionThisLoop()
    {
        Handler.Collection a = new Handler.Collection();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> a.addHandler(a));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testHandlerCollectionDeepLoop()
    {
        Handler.Collection a = new Handler.Collection();
        Handler.Collection b = new Handler.Collection();
        Handler.Collection b1 = new Handler.Collection();
        Handler.Collection b2 = new Handler.Collection();
        Handler.Collection c = new Handler.Collection();
        Handler.Collection c1 = new Handler.Collection();
        Handler.Collection c2 = new Handler.Collection();

        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(b1, b2);
        c.setHandlers(c1, c2);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> b2.addHandler(a));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testHandlerCollectionChainLoop()
    {
        Handler.Collection a = new Handler.Collection();
        Handler.Collection b = new Handler.Collection();
        Handler.Collection b1 = new Handler.Collection();
        Handler.Collection b2 = new Handler.Collection();
        Handler.Collection c = new Handler.Collection();
        Handler.Collection c1 = new Handler.Collection();
        Handler.Collection c2 = new Handler.Collection();

        a.addHandler(c);
        b.setHandlers(b1, b2);
        c.setHandlers(c1, c2);
        b2.addHandler(a);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> a.addHandler(b));
        assertThat(e.getMessage(), containsString("loop"));
    }

    @Test
    public void testInsertWrapperTail()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();

        a.insertHandler(b);
        assertThat(a.getHandler(), equalTo(b));
        assertThat(b.getHandler(), nullValue());
    }

    @Test
    public void testInsertWrapper()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();

        a.insertHandler(c);
        a.insertHandler(b);
        assertThat(a.getHandler(), equalTo(b));
        assertThat(b.getHandler(), equalTo(c));
        assertThat(c.getHandler(), nullValue());
    }

    @Test
    public void testInsertWrapperChain()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();
        Handler.Wrapper d = new Handler.Wrapper();

        a.insertHandler(d);
        b.insertHandler(c);
        a.insertHandler(b);
        assertThat(a.getHandler(), equalTo(b));
        assertThat(b.getHandler(), equalTo(c));
        assertThat(c.getHandler(), equalTo(d));
        assertThat(d.getHandler(), nullValue());
    }

    @Test
    public void testInsertWrapperBadChain()
    {
        Handler.Wrapper a = new Handler.Wrapper();
        Handler.Wrapper b = new Handler.Wrapper();
        Handler.Wrapper c = new Handler.Wrapper();
        Handler.Wrapper d = new Handler.Wrapper();

        a.insertHandler(d);
        b.insertHandler(c);
        c.setHandler(new Handler.Abstract()
        {
            @Override
            public Request.Processor handle(Request request)
            {
                return null;
            }
        });

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> a.insertHandler(b));
        assertThat(e.getMessage(), containsString("bad tail"));
    }

    @Test
    public void testSetServerPropagation()
    {
        Handler.Wrapper wrapper = new Handler.Wrapper();
        Handler.Collection collection = new Handler.Collection();
        Handler handler = new Handler.Abstract()
        {
            @Override
            public Request.Processor handle(Request request) throws Exception
            {
                return null;
            }
        };

        collection.addHandler(wrapper);
        wrapper.setHandler(handler);

        Server server = new Server();
        collection.setServer(server);

        assertThat(handler.getServer(), sameInstance(server));
    }

    @Test
    public void testSetHandlerServerPropagation()
    {
        Handler.Wrapper wrapper = new Handler.Wrapper();
        Handler.Collection collection = new Handler.Collection();
        Handler handler = new Handler.Abstract()
        {
            @Override
            public Request.Processor handle(Request request) throws Exception
            {
                return null;
            }
        };

        Server server = new Server();
        collection.setServer(server);

        collection.addHandler(wrapper);
        wrapper.setHandler(handler);

        assertThat(handler.getServer(), sameInstance(server));
    }

    @Test
    public void testConditional() throws Exception
    {
        Server server = new Server();

        Handler.Conditional conditional = new Handler.Conditional(List.of(
            Request.forMethod("GET"),
            Request.forPath(List.of("/include/*"), List.of("*.excluded"))));

        server.setHandler(conditional);
        conditional.setHandler(new HelloHandler());

        LocalConnector local = new LocalConnector(server);
        server.addConnector(local);
        server.start();

        assertThat(local.getResponse("GET / HTTP/1.0\r\n\r\n"), containsString(" 404 "));
        assertThat(local.getResponse("GET /include/path HTTP/1.0\r\n\r\n"), containsString(" 200 "));
        assertThat(local.getResponse("GET /include/path.excluded HTTP/1.0\r\n\r\n"), containsString(" 404 "));
        assertThat(local.getResponse("POST /include/path HTTP/1.0\r\n\r\n"), containsString(" 404 "));
    }

    @Test
    public void testConditionalSkipNext() throws Exception
    {
        Server server = new Server();

        Handler.Conditional conditional = new Handler.Conditional(true, List.of(
            Request.forMethod("GET"),
            Request.forPath(List.of("/include/*"), List.of("*.excluded"))));

        server.setHandler(conditional);

        // A wrapper that may or may not modify the request/response.  Like GzipHandler
        Handler.Wrapper wrapper = new Handler.Wrapper()
        {
            @Override
            public Request.Processor handle(Request request) throws Exception
            {
                Request.Processor processor = super.handle(request);
                if (processor == null)
                    return null;
                return (req, res, cb) ->
                {
                    res.getHeaders().add("Wrapper", "applied");
                    processor.process(req, res, cb);
                };
            }
        };

        conditional.setHandler(wrapper);
        wrapper.setHandler(new HelloHandler());

        LocalConnector local = new LocalConnector(server);
        server.addConnector(local);
        server.start();

        assertThat(local.getResponse("GET / HTTP/1.0\r\n\r\n"), not(containsString("Wrapper: applied")));
        assertThat(local.getResponse("GET /include/path HTTP/1.0\r\n\r\n"), containsString("Wrapper: applied"));
        assertThat(local.getResponse("GET /include/path.excluded HTTP/1.0\r\n\r\n"), not(containsString("Wrapper: applied")));
        assertThat(local.getResponse("POST /include/path HTTP/1.0\r\n\r\n"), not(containsString("Wrapper: applied")));
    }
}
