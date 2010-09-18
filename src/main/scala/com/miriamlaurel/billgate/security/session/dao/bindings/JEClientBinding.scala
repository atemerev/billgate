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
        val client = new Client;
        val id = tupleInput.readString();
        //TODO set id of client
        val calendar = Calendar.getInstance();
        calendar setTimeInMillis(tupleInput.readLong()*1000);
        val accessDate = calendar.getTime();
        new ClientAccessTimeWrapper(client,accessDate);
    }

    override def objectToEntry(client : ClientAccessTimeWrapper, tupleOutput : TupleOutput) {
        val realClient = client getClient;
        val accessDate = client getAccessDate;
        tupleOutput writeString(String.valueOf(realClient.id()));
        tupleOutput writeLong(accessDate.getTime());
    }

}
