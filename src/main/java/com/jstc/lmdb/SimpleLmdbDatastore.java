package com.jstc.lmdb;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lmdbjava.DbiFlags.MDB_CREATE;

import java.io.File;
import java.nio.ByteBuffer;

import org.lmdbjava.Dbi;
import org.lmdbjava.Env;
import org.lmdbjava.Txn;

import com.jstc.datastore.Datastore;
import com.jstc.datastore.MutableDatastore;

/**
 * Simplest impl of {@link Datastore} using LMDB as backing datastore. Limited to
 * a maximum size of 100MB (could be refactored to allow larger).
 */
public class SimpleLmdbDatastore implements MutableDatastore<String> {

    /**
     * LMDB also needs to know how large our DB might be. Over-estimating is OK. Use
     * 100 MB default for now
     */
    private static final long DEFAULT_SIZE_BYTES = 100 * 1024 * 1024;
    
    /**
     *  We always need an Env. An Env owns a physical on-disk storage file. One
     *  Env can store many different databases (ie sorted maps).
     */
    private final Env<ByteBuffer> env;
    
    /**
     * We need a Dbi for each DB. A Dbi roughly equates to a sorted map. The
     * MDB_CREATE flag causes the DB to be created if it doesn't already exist.
     */
    private final Dbi<ByteBuffer> db; 
    
    /**
     * @param dir           directory path (must exist) to store the database
     *                      contents. The same path can be concurrently opened and
     *                      used in different processes, but do not open the same
     *                      path twice in the same process at the same time.
     * @param maxReadThreads maximum number of threads that will read from LMDB at once
     *                      If more threads try to read, {@link #get(String)} will throw 
     *                      org.lmdbjava.Env$ReadersFullException: Environment maxreaders reached (-30790)
     */
    public SimpleLmdbDatastore(final File dir, int maxReadThreads) {
        checkNotNull(dir);
        checkState(dir.exists(), "dir must exist");
        checkState(dir.isDirectory(), "dir must represent a directory");
        
        this.env = Env.create()
            .setMapSize(DEFAULT_SIZE_BYTES)
            .setMaxDbs(1)
            .setMaxReaders(maxReadThreads)
            .open(dir);
        
        this.db = env.openDbi((String)null, MDB_CREATE);
    }
    
    @Override
    public String get(String key) {
        checkNotNull(key);
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            final ByteBuffer result = db.get(txn, stringToBuffer(key));
            if (result == null) {
                return null;
            }
            return bufferToString(result);
        } 
        
    }

    @Override
    public void put(String key, String value) {
        checkNotNull(key);
        checkNotNull(value);
        db.put(stringToBuffer(key), stringToBuffer(value));
    }

    private static ByteBuffer stringToBuffer(String input) {
        ByteBuffer result = ByteBuffer.allocateDirect(input.length());
        result.put(input.getBytes(UTF_8)).flip();
        return result;
    }
    
    private static String bufferToString(ByteBuffer input) {
        return UTF_8.decode(input).toString();
    }
}
