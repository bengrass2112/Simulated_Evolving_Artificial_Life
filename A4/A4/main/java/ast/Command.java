package ast;

public class Command extends AbstractNode {

	// the value is null
	public Command() {
		super();
		value = null;
	}

	/**
	 * 
	 * @return true if has a 1 or more children
	 */
	public boolean empty() {
		if (getChildren().size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @effect adds an action to the end of the list
	 */
	public void addAction(Action act) {
		children.add(act);
		act.setParent(this);
	}

	/**
	 * 
	 * @effect adds the update u to the children List
	 */
	public void addUpdate(Update u) {
		children.add(u);
		u.setParent(this);
	}

	public Node shallowClone() {
		return new Command();
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		for (Node child : children) {
			child.prettyPrint(sb);
		}
		return sb;
	}

}
