package pta.toolkit;

import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;

import java.util.List;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-24 16:11
 **/


public class ConstantValue implements Value ,Data{
    @Override
    public List<ValueBox> getUseBoxes() {
        return null;

    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public void toString(UnitPrinter unitPrinter) {

    }

    @Override
    public boolean equivTo(Object o) {
        return false;
    }

    @Override
    public int equivHashCode() {
        return 0;
    }

    @Override
    public void apply(Switch aSwitch) {

    }

    @Override
    public String getName() {
        return null;
    }
}
