package application;

public class Pair<T1, T2> {
	private T1 x;
	private T2 y;

	public Pair (T1 x, T2 y) {
		this.x = x;
		this.y = y;
	}

	public T1 X () {
		return this.x;
	}

	public T2 Y () {
		return this.y;
	}

	public void setX (T1 x) {
		this.x = x;
	}

	public void setY (T2 y) {
		this.y = y;
	}

	@Override	
	public String toString() {
		return "{X: " + (this.x).toString() + ",Y: " + (this.y).toString() + "}";
	}
	
	@Override
	public int hashCode() {
		return x.hashCode() + y.hashCode();
	}
}