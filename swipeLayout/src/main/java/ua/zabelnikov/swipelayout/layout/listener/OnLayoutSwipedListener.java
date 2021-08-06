package ua.zabelnikov.swipelayout.layout.listener;

public interface OnLayoutSwipedListener {
    int FLING = 0;
    int SWIPE = 1;

    void onLayoutSwiped(int swipedFrom);

}
