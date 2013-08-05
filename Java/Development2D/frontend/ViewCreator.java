package frontend;

import view.EmbeddedView;
import view.ExponentialView;
import view.FirstPersonView;

public interface ViewCreator {
	public void addAllViewsController(ViewController vc);
	public void addExponentialViewController(ViewController vc);
	public void addEmbeddedViewController(ViewController vc);
	public void addFirstPersonViewController(ViewController vc);
	
	public ExponentialView createExponentialView();
	public EmbeddedView createEmbeddedView();
	public FirstPersonView createFirstPersonView();
}
