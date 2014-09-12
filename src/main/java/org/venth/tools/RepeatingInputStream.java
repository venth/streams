package org.venth.tools;

import java.io.IOException;
import java.io.InputStream;

/**
 * The goal of this class is to repeat given array until given size limit
 * is reached.
 *
 * For example you can test your parsing class with a stream of a size 60GB without
 * need to provide such big file
 */
public class RepeatingInputStream extends InputStream
{
    private final byte[] content;
    private final int contentLength;
    private final long streamSize;
    private long position;

    /**
     * creates a stream that will repeat the given content until sizeLimit is reached
     * @param content a content to repeat
     * @param sizeLimit a limit in bytes
     */
    public RepeatingInputStream(byte[] content, long sizeLimit) {

        if (sizeLimit < 0) {
            throw new IllegalArgumentException("Cannot limit the stream with negative values. The limit value must be positive or equals zero");
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Content array must contain at least one element");
        }

        this.content = content;
        contentLength = content.length;
        streamSize = ((sizeLimit + contentLength - 1) / contentLength) * contentLength;
        position = 0;
    }

    @Override
    public int read() throws IOException {
        int element = -1;

        if (position < streamSize) {
            element = content[((int) (position % contentLength))];
            position = position + 1;
        }

        return element;
    }
}
