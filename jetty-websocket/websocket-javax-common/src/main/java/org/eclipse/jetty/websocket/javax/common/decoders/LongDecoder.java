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

package org.eclipse.jetty.websocket.javax.common.decoders;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;

/**
 * Default implementation of the Text Message to {@link Long} decoder
 */
public class LongDecoder extends AbstractDecoder implements Decoder.Text<Long>
{
    public static final LongDecoder INSTANCE = new LongDecoder();

    @Override
    public Long decode(String s) throws DecodeException
    {
        try
        {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e)
        {
            throw new DecodeException(s, "Unable to parse Long", e);
        }
    }

    @Override
    public boolean willDecode(String s)
    {
        if (s == null)
        {
            return false;
        }
        try
        {
            Long.parseLong(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}
