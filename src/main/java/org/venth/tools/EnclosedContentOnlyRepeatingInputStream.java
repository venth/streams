package org.venth.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

/**
 * this class repeats only a content which is provided
 * between a head and a tail.
 *
 * So the results stream will be looks like that:
 * head, content .. until sizeLimit is reached, tail
 *
 * This class is closely collaborating with {@link RepeatingInputStream}
 * to provide repeatable streaming capabilities
 *
 * @author Artur Krysiak (last modified by $Author$).
 * @version $Revision$ $Date$
 */
public class EnclosedContentOnlyRepeatingInputStream extends InputStream {
    private final SequenceInputStream enclosedStream;

    /**
     * Creates a stream that will first stream a head, then repeat the content and after
     * the size limit is reached, the stream will provide the tail
     *
     * this constructor is only a facade to {@link #EnclosedContentOnlyRepeatingInputStream(byte[], RepeatingInputStream, byte[])}
     *
     * @param head a head to stream before the content
     * @param content a content to be repeated until size limit is reached
     * @param tail a tail to stream when the content streaming is stopped due to limit reaching
     * @param sizeLimit a content's size limit in bytes
     */
    public EnclosedContentOnlyRepeatingInputStream(byte[] head, byte[] content, byte[] tail, long sizeLimit) {
        this(head, new RepeatingInputStream(content, sizeLimit), tail);
    }


    public EnclosedContentOnlyRepeatingInputStream(byte[] head, RepeatingInputStream repeatingContentStream, byte[] tail) {
        enclosedStream = new SequenceInputStream(
                new SequenceInputStream(new ByteArrayInputStream(head), repeatingContentStream),
                new ByteArrayInputStream(tail)
        );
    }


    @Override
    public int read() throws IOException {
        return enclosedStream.read();
    }
}
