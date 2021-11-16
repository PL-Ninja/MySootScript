package utils;

import soot.tagkit.AbstractAnnotationElemTypeSwitch;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.util.annotations.AnnotationInstanceCreator;
import soot.util.annotations.ClassLoaderUtils;

import java.util.HashMap;

/**
 *
 * An {@link AbstractAnnotationElemTypeSwitch} that converts an {@link AnnotationElem} to a mapping of element name and the
 * actual result.
 *
 * @author Florian Kuebler
 *
 */
public class AnnotationElemSwitch extends AbstractAnnotationElemTypeSwitch {

    /**
     *
     * A helper class to map method name and result.
     *
     * @author Florian Kuebler
     *
     * @param <V>
     *          the result type.
     */
    public class AnnotationElemResult<V> {

        private String name;
        private V value;

        public AnnotationElemResult(String name, V value) {
            this.name = name;
            this.value = value;
        }

        public String getKey() {
            return name;
        }

        public V getValue() {
            return value;
        }
    }
// AnnotationArrayElem 与 AnnotationAnnotationElem 都嵌套了AnnotationTag 后续根据需求修改
//    @Override
//    public void caseAnnotationAnnotationElem(AnnotationAnnotationElem v) {
//        AnnotationInstanceCreator aic = new AnnotationInstanceCreator();
//
//        Object result = aic.create(v.getValue());
//
//        setResult(new AnnotationElemResult<Object>(v.getName(), result));
//    }
//
    @Override
    public void caseAnnotationArrayElem(AnnotationArrayElem v) {

        /*
         * for arrays, apply a new AnnotationElemSwitch to every array element and collect the results. Note that the component
         * type of the result is unknown here, s.t. object has to be used.
         */
        AnnotationElemResult[] result = new AnnotationElemResult[v.getNumValues()];

        int i = 0;
        for (AnnotationElem elem : v.getValues()) {
            AnnotationElemSwitch sw = new AnnotationElemSwitch();
            elem.apply(sw);
            result[i] = (AnnotationElemResult) sw.getResult();

            i++;
        }
        setResult(new AnnotationElemResult<Object[]>(v.getName(), result));

    }

    @Override
    public void caseAnnotationBooleanElem(AnnotationBooleanElem v) {
        setResult(new AnnotationElemResult<Boolean>(v.getName(), v.getValue()));

    }

    @Override
    public void caseAnnotationClassElem(AnnotationClassElem v) {

        setResult(new AnnotationElemResult<String>(v.getName(), v.getDesc()));


    }

    @Override
    public void caseAnnotationDoubleElem(AnnotationDoubleElem v) {
        setResult(new AnnotationElemResult<Double>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationEnumElem(AnnotationEnumElem v) {

        HashMap<String, String> result = new HashMap<>();
        result.put("typeName", v.getTypeName());
        result.put("constantName", v.getConstantName());
        setResult(new AnnotationElemResult<HashMap>(v.getName(), result));


    }

    @Override
    public void caseAnnotationFloatElem(AnnotationFloatElem v) {
        setResult(new AnnotationElemResult<Float>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationIntElem(AnnotationIntElem v) {
        setResult(new AnnotationElemResult<Integer>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationLongElem(AnnotationLongElem v) {
        setResult(new AnnotationElemResult<Long>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationStringElem(AnnotationStringElem v) {
        setResult(new AnnotationElemResult<String>(v.getName(), v.getValue()));
    }

    @Override
    public void defaultCase(Object object) {
        throw new RuntimeException("Unexpected AnnotationElem");
    }

}
