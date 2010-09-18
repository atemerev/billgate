package com.miriamlaurel.billgate.security.session.dao.bindings;

import com.miriamlaurel.billgate.security.session.Session;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

class JESessionBinding extends TupleBinding[Session] {

    override def entryToObject(input : TupleInput) : Session = {
        new Session(input.readString());
    }

    override def objectToEntry(session : Session, output : TupleOutput) {
        output writeString(session.sessionId);
    }

}
