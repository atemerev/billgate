package com.miriamlaurel.billgate.security.session.dao.bindings;

import com.miriamlaurel.billgate.model.Client;
import com.miriamlaurel.billgate.security.session.entity.ClientAccessTimeWrapper;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.util.Calendar;
import java.util.Date;

public class JEClientBinding extends TupleBinding<ClientAccessTimeWrapper> {

    @Override
    public ClientAccessTimeWrapper entryToObject(TupleInput tupleInput) {
        Client client = new Client();
        String id = tupleInput.readString();
        //TODO set id of client
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tupleInput.readLong()*1000);
        Date accessDate = calendar.getTime();
        return new ClientAccessTimeWrapper(client,accessDate);
    }

    @Override
    public void objectToEntry(ClientAccessTimeWrapper client, TupleOutput tupleOutput) {
        Client realClient = client.getClient();
        Date accessDate = client.getAccessDate();
        tupleOutput.writeString(String.valueOf(realClient.id()));
        tupleOutput.writeLong(accessDate.getTime());
    }

}
