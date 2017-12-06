package org.juel.analysis;

import org.nd4j.linalg.api.ndarray.BaseNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;

public class JuelArray extends BaseNDArray {

    @Override
    public INDArray unsafeDuplication() {
        JuelArray array =  new JuelArray();
        array.setData(this.data);
        return array;
    }

    @Override
    public INDArray unsafeDuplication(boolean blocking) {
        JuelArray array =  new JuelArray();
        array.setData(this.data);
        return array;
    }
}
