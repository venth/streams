package org.venth.tools

import spock.lang.Specification

/**
 * @author Artur Krysiak (last modified by $Author$).
 * @version $Revision$ $Date$
 */
class EnclosedContentOnlyRepeatingInputStreamTest extends Specification {
    def "head is streamed before the content's stream"() {
        given: 'a head'
        def head = "Yoda".bytes

        and: 'content stream'
        def conentStream = Mock RepeatableInputStream

        and: 'a tail'
        def tail = "Master".bytes

        and: 'a content'

        when:

    }

    def "tail is streamed after the content's stream"() {


    }
}
