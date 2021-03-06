package tech.mlsql.common.utils.hash;

import tech.mlsql.common.utils.primitives.Chars;
import tech.mlsql.common.utils.primitives.Ints;
import tech.mlsql.common.utils.primitives.Longs;
import tech.mlsql.common.utils.primitives.Shorts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;
import static tech.mlsql.common.utils.base.Preconditions.checkPositionIndexes;

/**
 * Abstract {@link Hasher} that handles converting primitives to bytes using a scratch {@code
 * ByteBuffer} and streams all bytes to a sink to compute the hash.
 *
 * @author Colin Decker
 */
abstract class AbstractByteHasher extends AbstractHasher {

    private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);

    /**
     * Updates this hasher with the given byte.
     */
    protected abstract void update(byte b);

    /**
     * Updates this hasher with the given bytes.
     */
    protected void update(byte[] b) {
        update(b, 0, b.length);
    }

    /**
     * Updates this hasher with {@code len} bytes starting at {@code off} in the given buffer.
     */
    protected void update(byte[] b, int off, int len) {
        for (int i = off; i < off + len; i++) {
            update(b[i]);
        }
    }

    @Override
    public Hasher putByte(byte b) {
        update(b);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] bytes) {
        checkNotNull(bytes);
        update(bytes);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] bytes, int off, int len) {
        checkPositionIndexes(off, off + len, bytes.length);
        update(bytes, off, len);
        return this;
    }

    /**
     * Updates the sink with the given number of bytes from the buffer.
     */
    private Hasher update(int bytes) {
        try {
            update(scratch.array(), 0, bytes);
        } finally {
            scratch.clear();
        }
        return this;
    }

    @Override
    public Hasher putShort(short s) {
        scratch.putShort(s);
        return update(Shorts.BYTES);
    }

    @Override
    public Hasher putInt(int i) {
        scratch.putInt(i);
        return update(Ints.BYTES);
    }

    @Override
    public Hasher putLong(long l) {
        scratch.putLong(l);
        return update(Longs.BYTES);
    }

    @Override
    public Hasher putChar(char c) {
        scratch.putChar(c);
        return update(Chars.BYTES);
    }

    @Override
    public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
        funnel.funnel(instance, this);
        return this;
    }
}
