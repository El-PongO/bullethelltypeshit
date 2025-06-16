/**
 * Interface for hero select panels
 */
public interface IHeroSelectPanel {
    /**
     * Listener interface for hero selection events
     */
    interface HeroSelectListener {
        void onHeroSelected(String heroName);
    }
}
