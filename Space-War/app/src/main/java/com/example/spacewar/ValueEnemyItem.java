package com.example.spacewar;

public class ValueEnemyItem extends GameItem{
    private int value;
    private boolean isExplode;
    private boolean isIconOriginal;

    public ValueEnemyItem(int value, int speed) {
        super(speed);
        setValue(value);
        isExplode = false;
        isIconOriginal = true;
    }

    public boolean isIconOriginal() {
        return isIconOriginal;
    }

    public void setIconOriginal(boolean iconOriginal) {
        isIconOriginal = iconOriginal;
    }

    public boolean isExplode() {
        return isExplode;
    }

    public void setExplode(boolean explode) {
        isExplode = explode;
    }

    public int getValue() {
        setValue(value);
        return value;
    }

    public void setValue(int value) {
        this.value = value+(int)Math.floor((1000-super.getSpeed())/100); //bonus for killing fast enemies or collect fast gift
    }
}
