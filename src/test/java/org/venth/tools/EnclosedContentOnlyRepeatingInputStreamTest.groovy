package org.venth.tools

import com.google.common.collect.ComparisonChain
import com.google.common.collect.ObjectArrays
import com.google.common.collect.Ordering
import spock.lang.Specification

/**
 * @author Artur Krysiak (last modified by $Author$).
 * @version $Revision$ $Date$
 */
class EnclosedContentOnlyRepeatingInputStreamTest extends Specification {
    def "head is streamed before the content's stream"() {
        given: 'a head'
        def head = "Yoda".bytes

        and: 'content stream returns one byte'
        def contentStream = Mock RepeatingInputStream
        contentStream.read() >>> [ 32, -1 ]

        and: 'a tail'
        def tail = "Master".bytes

        and: 'the enclosed stream'
        def enclosedStream = new EnclosedContentOnlyRepeatingInputStream(head, contentStream, tail)

        when: 'all the stream is read'
        def wholeStream = getWholeStreamContent(enclosedStream)

        then: 'read stream is bigger than the head'
        wholeStream.length > head.length

        then: 'head was read as first'
        0 == ComparisonChain.start()
            .compare(Arrays.copyOf(wholeStream, head.length), head, Ordering.<Byte>allEqual())
            .result()
    }

    def "tail is streamed after the content's stream"() {
        given: 'a head'
        def head = "Yoda".bytes

        and: 'content stream returns one byte'
        def contentStream = Mock RepeatingInputStream
        contentStream.read() >>> [32, -1]

        and: 'a tail'
        def tail = "Master".bytes

        and: 'the enclosed stream'
        def enclosedStream = new EnclosedContentOnlyRepeatingInputStream(head, contentStream, tail)

        when: 'all the stream is read'
        def wholeStream = getWholeStreamContent(enclosedStream)

        then: 'read stream is bigger than the head and tail'
        wholeStream.length > (head.length + tail.length)

        then: 'tail was read at the end'
        def readTail = new byte[tail.length]
        //to satisfy spock ;)
        null == System.arraycopy(wholeStream, wholeStream.length - tail.length - 1, readTail, 0, tail.length)

        0 == ComparisonChain.start()
                .compare(readTail, tail, Ordering.<Byte>allEqual())
                .result()
    }

    def "content is provided between head and tail"() {
        given: 'a head'
        def head = "Yoda".bytes

        and: 'content stream returns one byte'
        def contentStream = Mock RepeatingInputStream
        contentStream.read() >>> [32, -1]

        and: 'a tail'
        def tail = "Master".bytes

        and: 'the enclosed stream'
        def enclosedStream = new EnclosedContentOnlyRepeatingInputStream(head, contentStream, tail)

        when: 'all the stream is read'
        def wholeStream = getWholeStreamContent(enclosedStream)

        then: 'read stream size equals sum of length of head, content and the tail'
        wholeStream.length == (head.length + 1 + tail.length)

        then: 'exactly one byte of content was read'
        wholeStream[head.length] == 32
    }

    def "for empty content stream only head and tail are streamed"() {
        given: 'a head'
        def head = "Yoda".bytes

        and: 'content stream is empty'
        def contentStream = Mock RepeatingInputStream
        contentStream.read() >> -1

        and: 'a tail'
        def tail = "Master".bytes

        and: 'the enclosed stream'
        def enclosedStream = new EnclosedContentOnlyRepeatingInputStream(head, contentStream, tail)

        when: 'all the stream is read'
        def wholeStream = getWholeStreamContent(enclosedStream)

        then: 'read stream size equals sum of length of head and the tail'
        wholeStream.length == (head.length + tail.length)
    }


    byte[] getWholeStreamContent(EnclosedContentOnlyRepeatingInputStream enclosedStream) {
        def readContent = []
        def byte element
        while ((element = enclosedStream.read()) != -1) {
            readContent = readContent + element
        }

        readContent.toArray(new byte[0])
    }
}
