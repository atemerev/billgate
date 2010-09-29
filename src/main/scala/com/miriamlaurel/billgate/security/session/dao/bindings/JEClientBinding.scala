package com.miriamlaurel.billgate.security.session.dao.bindings;

import com.miriamlaurel.billgate.model.Client;
import com.miriamlaurel.billgate.security.session.entity.ClientAccessTimeWrapper;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.util.Calendar;
import java.util.Date;

class JEClientBinding extends TupleBinding[ClientAccessTimeWrapper] {

    override def entryToObject(tupleInput : TupleInput) : ClientAccessTimeWrapper = {
        val clientLogin = tupleInput.readString();
        val calendar = Calendar.getInstance();
        calendar setTimeInMillis(tupleInput.readLong());
        val accessDate = calendar.getTime();
        new ClientAccessTimeWrapper(clientLogin,accessDate);
    }

    override def objectToEntry(client : ClientAccessTimeWrapper, tupleOutput : TupleOutput) {
        val clientLogin = client getClientLogin;
        val accessDate = client getAccessDate;
        tupleOutput writeString(clientLogin);
        tupleOutput writeLong(accessDate.getTime());
    }

}
