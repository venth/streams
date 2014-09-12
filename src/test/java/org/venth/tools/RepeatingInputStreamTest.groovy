package org.venth.tools

import com.google.common.collect.ComparisonChain
import com.google.common.collect.ObjectArrays
import com.google.common.collect.Ordering
import spock.lang.Specification

/**
 * @author Artur Krysiak (last modified by $Author$).
 * @version $Revision$ $Date$
 */
class RepeatingInputStreamTest extends Specification {
    def "content bigger than given size limit is served as whole before reading stops"() {
        given: 'repeatable content contains: "AAA"'
        def byte[] content = "AAA".bytes

        and: 'size limit is less than the repeatable content size and greater than zero'
        def sizeLimit = content.length - 1;

        and: 'repeatable input stream is created'
        def InputStream repeatableStream = new RepeatingInputStream(content, sizeLimit)

        when: 'read the stream until reaches an end'
        def readBytesCount = 0
        while (repeatableStream.read() != -1) {
            readBytesCount++
        }

        then: 'read exactly the same number of bytes that are in content'
        readBytesCount == content.length
    }

    def "when limited size is zero then nothing is streamed"() {
        given: 'repeatable content contains: "AAA"'
        def byte[] content = "AAA".bytes

        and: 'size limit is 0'
        def sizeLimit = 0;

        and: 'repeatable input stream is created'
        def repeatingStream = new RepeatingInputStream(content, sizeLimit)

        when: 'one byte is read from the stream'
        def element = repeatingStream.read()

        then: "nothing was read"
        element == -1
    }

    def "when limited size is negative then IllegalArgument is raised"() {
        given: 'repeatable content contains: "AAA"'
        def byte[] content = "AAA".bytes

        and: 'size limit is -1 (negative)'
        def sizeLimit = -1;

        when: 'repeatable input stream is created'
        new RepeatingInputStream(content, sizeLimit)

        then: "cannot limit the stream with negative values"
        def e = thrown IllegalArgumentException
        e.message ==  'Cannot limit the stream with negative values. The limit value must be positive or equals zero'
    }

    def "content is repeated n times when the limit size is n times bigger than the content's size"() {
        given: 'repeatable content contains: "AAA"'
        def byte[] content = "AAA".bytes

        and: 'size limit is content length multiplied ${times} times'
        def sizeLimit = content.length * times

        and: "repeatable input stream is created"
        def InputStream repeatableStream = new RepeatingInputStream(content, sizeLimit)

        when: 'whole stream is read'
        def readBytesCount = 0
        while (repeatableStream.read() != -1) {
            readBytesCount++
        }

        then: "number of bytes read from a stream is ${times} times of the content's length"
        content.length * times == readBytesCount

        where:
        times << [ 1, 2, 3, 4, 5, 6, 7, 8, 100 ]
    }

    def "stream returns an array content in the same order as was declared in the array"() {
        given: 'a content that will be repeated'
        def byte[] content = textToRepeat.bytes

        and: 'size limit equals content length'
        def sizeLimit = content.length

        and: "repeatable input stream is created"
        def InputStream repeatableStream = new RepeatingInputStream(content, sizeLimit)

        when: 'whole stream is read'
        def readContent = [] as byte[]
        def byte element
        while ((element = repeatableStream.read()) != -1) {
            ObjectArrays.concat(readContent, element)
        }

        then: "read array is exactly the same as original content"
        0 == ComparisonChain.start()
                .compare(content, readContent, Ordering.<Byte>allEqual())
                .result()

        where:
        textToRepeat << [ "asd", "asdasueoijdasd", "asdasdaw333", "asdasdasdads3jfl" ]
    }

    def "stream repeats the array when is bigger than the array"() {
        given: 'a content that will be repeated'
        def byte[] content = textToRepeat.bytes

        and: 'size limit equals content length multiplied by ${times} times'
        def sizeLimit = content.length

        and: "repeatable input stream is created"
        def InputStream repeatableStream = new RepeatingInputStream(content, sizeLimit)

        when: 'whole stream is read'
        def readContent = [] as byte[]
        def byte element
        while ((element = repeatableStream.read()) != -1) {
            readContent = ObjectArrays.concat(readContent, element)
        }

        then: "read array is exactly the same as original content repeated ${times} times"
        0 == ComparisonChain.start()
            .compare(getCloned(content, times), readContent, Ordering.<Byte>allEqual())
            .result()

        where:
        textToRepeat    | times
        "ASdasd"        | 2
        "Ddasasdasdf"   | 3
    }

    def "content array must contain at least one element"() {
        given: 'an empty content'
        def byte[] content = [] as byte[]

        and: 'size limit is 100 - just simple greater than zero'
        def sizeLimit = 100

        when: "repeatable input stream is created"
        new RepeatingInputStream(content, sizeLimit)

        then: "content cannot be empty"
        def e = thrown IllegalArgumentException
        e.message == 'Content array must contain at least one element'
    }

    def "content array cannot be null"() {
        given: 'a content without any value - null'
        def byte[] content = null

        and: 'size limit is 100 - just simple greater than zero'
        def sizeLimit = 100

        when: "repeatable input stream is created"
        new RepeatingInputStream(content, sizeLimit)

        then: "content cannot be null"
        def e = thrown IllegalArgumentException
        e.message == 'Content array must contain at least one element'
    }

    byte[] getCloned(byte[] content, int times) {
        def byte[] result = Arrays.copyOf(content, content.length * times)
        def position = 0
        while (position < times * content.length) {
            result[position] = content[position % content.length]
            position = position + 1
        }

        result
    }
}
