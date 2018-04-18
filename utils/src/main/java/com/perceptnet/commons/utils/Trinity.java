package com.perceptnet.commons.utils;

/**
 * Class made final in setOrder to stop here - i.e. not to have Quad etc.
 *
 * Created by vkorovkin on 01.07.2015.
 */
public final class Trinity<FIRST, SECOND, THIRD> extends Pair<FIRST, SECOND> {
    private final THIRD third;

    public Trinity(FIRST first, SECOND second, THIRD third) {
        super(first, second);
        this.third = third;
    }

    public THIRD getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Trinity trinity = (Trinity) o;

        if (third != null ? !third.equals(trinity.third) : trinity.third != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37 * result + (third != null ? third.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Trinity[" + getFirst() + ", " + getSecond() + ", " + getThird() + "]";
    }

}
