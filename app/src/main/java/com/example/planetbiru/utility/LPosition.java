package com.example.planetbiru.utility;

class LPositon {

    private int _vL;
    private int _position;

    public LPositon(int _vL, int position) {
        this._vL = _vL;
        this._position = position;
    }

    public int get_vL() {
        return _vL;
    }

    public void set_vL(int _vL) {
        this._vL = _vL;
    }

    public int get_position() {
        return _position;
    }

    public void set_position(int _position) {
        this._position = _position;
    }
}