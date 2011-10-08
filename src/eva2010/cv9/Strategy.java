package eva2010.cv9;

public abstract class Strategy {

    public abstract String getName();

    public abstract String authorName();

    public abstract Move nextMove();

    public abstract void reward(Result result);

    public abstract void reset();

    public enum Move {
        COOPERATE ("C"),
        DECEIVE ("D");

        private String label;

        Move(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
