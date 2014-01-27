package fast.rocket.builder;

public interface Builders {

    public interface ImageView {
        public interface F<A extends F<?>> extends ImageViewBuilder<A>, BitmapBuilder<A> {
        }
    }

}
