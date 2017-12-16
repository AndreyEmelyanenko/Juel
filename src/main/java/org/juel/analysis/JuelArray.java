package org.juel.analysis;

import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.BaseNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public class JuelArray extends BaseNDArray {

    @Override
    public INDArray unsafeDuplication() {
        JuelArray array =  new JuelArray(this.data);
        return array;
    }

    @Override
    public INDArray unsafeDuplication(boolean blocking) {
        JuelArray array =  new JuelArray(this.data);
        return array;
    }

    @Override
    public String toString() {
        return "JuelArray{" + super.data+ "}";
    }


    public JuelArray(DataBuffer buffer) {
        super(buffer);
    }

    public JuelArray(DataBuffer buffer, int[] shape, int[] stride, long offset, char ordering) {
        super(buffer, shape, stride, offset, ordering);
    }

    public JuelArray(double[][] data) {
        super(data);
    }

    public JuelArray(double[][] data, char ordering) {
        super(data, ordering);
    }

    public JuelArray(int[] shape, DataBuffer buffer) {
        super(shape, buffer);
    }

    public JuelArray(float[] data, int[] shape, char ordering) {
        super(data, shape, ordering);
    }

    public JuelArray(float[] data, int[] shape, long offset, char ordering) {
        super(data, shape, offset, ordering);
    }

    public JuelArray(int[] shape, int[] stride, long offset, char ordering) {
        super(shape, stride, offset, ordering);
    }

    public JuelArray(int[] shape, int[] stride, long offset, char ordering, boolean initialize) {
        super(shape, stride, offset, ordering, initialize);
    }

    public JuelArray(int[] shape, int[] stride, char ordering) {
        super(shape, stride, ordering);
    }

    public JuelArray(int[] shape, long offset, char ordering) {
        super(shape, offset, ordering);
    }

    public JuelArray(int[] shape) {
        super(shape);
    }

    public JuelArray(int newRows, int newColumns, char ordering) {
        super(newRows, newColumns, ordering);
    }

    public JuelArray(List<INDArray> slices, int[] shape, char ordering) {
        super(slices, shape, ordering);
    }

    public JuelArray(List<INDArray> slices, int[] shape, int[] stride, char ordering) {
        super(slices, shape, stride, ordering);
    }

    public JuelArray(float[] data, int[] shape, int[] stride, char ordering) {
        super(data, shape, stride, ordering);
    }

    public JuelArray(float[] data, int[] shape, int[] stride, long offset, char ordering) {
        super(data, shape, stride, offset, ordering);
    }

    public JuelArray(DataBuffer data, int[] shape, int[] stride, long offset) {
        super(data, shape, stride, offset);
    }

    public JuelArray(int[] data, int[] shape, int[] strides) {
        super(data, shape, strides);
    }

    public JuelArray(DataBuffer data, int[] shape) {
        super(data, shape);
    }

    public JuelArray(DataBuffer buffer, int[] shape, long offset) {
        super(buffer, shape, offset);
    }

    public JuelArray(DataBuffer buffer, int[] shape, char ordering) {
        super(buffer, shape, ordering);
    }

    public JuelArray(double[] data, int[] shape, char ordering) {
        super(data, shape, ordering);
    }

    public JuelArray(double[] data, int[] shape, int[] stride, long offset, char ordering) {
        super(data, shape, stride, offset, ordering);
    }

    public JuelArray(float[] data, char order) {
        super(data, order);
    }

    public JuelArray(DataBuffer floatBuffer, char order) {
        super(floatBuffer, order);
    }

    public JuelArray(DataBuffer buffer, int[] shape, int[] strides) {
        super(buffer, shape, strides);
    }

    public JuelArray(float[] data, int[] shape) {
        super(data, shape);
    }

    public JuelArray(float[] data, int[] shape, long offset) {
        super(data, shape, offset);
    }

    public JuelArray(int[] shape, int[] stride, long offset) {
        super(shape, stride, offset);
    }

    public JuelArray(int[] shape, int[] stride) {
        super(shape, stride);
    }

    public JuelArray(int[] shape, long offset) {
        super(shape, offset);
    }

    public JuelArray(int[] shape, char ordering) {
        super(shape, ordering);
    }

    public JuelArray(int newRows, int newColumns) {
        super(newRows, newColumns);
    }

    public JuelArray(List<INDArray> slices, int[] shape) {
        super(slices, shape);
    }

    public JuelArray(List<INDArray> slices, int[] shape, int[] stride) {
        super(slices, shape, stride);
    }

    public JuelArray(float[] data, int[] shape, int[] stride) {
        super(data, shape, stride);
    }

    public JuelArray(float[] data, int[] shape, int[] stride, long offset) {
        super(data, shape, stride, offset);
    }

    public JuelArray(float[] data) {
        super(data);
    }

    public JuelArray(float[][] data) {
        super(data);
    }

    public JuelArray(float[][] data, char ordering) {
        super(data, ordering);
    }

    public JuelArray(DataBuffer buffer, int[] shape, long offset, char ordering) {
        super(buffer, shape, offset, ordering);
    }

    public JuelArray(double[] data, int[] shape, int[] stride, long offset) {
        super(data, shape, stride, offset);
    }

    public JuelArray() {
    }
}
