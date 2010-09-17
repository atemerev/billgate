package com.miriamlaurel.billgate.security.session.dao.bindings;

import com.miriamlaurel.billgate.security.session.Session;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class JESessionBinding extends TupleBinding<Session> {

    @Override
    public Session entryToObject(TupleInput input) {
        return new Session(input.readString());
    }

    public void objectToEntry(Session session, TupleOutput output) {
        output.writeString(session.sessionId());
    }

}
