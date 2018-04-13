package impl;

import impl.Controller;
import impl.Model;
import impl.View;
import interfaces.IEvent;
import interfaces.IRead;
import interfaces.IUpdate;
import interfaces.IUser;
import interfaces.IWrite;

@SuppressWarnings("all")
public abstract class Root {
  public interface Requires {
  }
  
  public interface Component extends Root.Provides {
  }
  
  public interface Provides {
    /**
     * This can be called to access the provided port.
     * 
     */
    public IUser user();
  }
  
  public interface Parts {
    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Model.Component model();
    
    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public View.Component view();
    
    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Controller.Component controller();
  }
  
  public static class ComponentImpl implements Root.Component, Root.Parts {
    private final Root.Requires bridge;
    
    private final Root implementation;
    
    public void start() {
      assert this.model != null: "This is a bug.";
      ((Model.ComponentImpl) this.model).start();
      assert this.view != null: "This is a bug.";
      ((View.ComponentImpl) this.view).start();
      assert this.controller != null: "This is a bug.";
      ((Controller.ComponentImpl) this.controller).start();
      this.implementation.start();
      this.implementation.started = true;
    }
    
    private void init_model() {
      assert this.model == null: "This is a bug.";
      assert this.implem_model == null: "This is a bug.";
      this.implem_model = this.implementation.make_model();
      if (this.implem_model == null) {
      	throw new RuntimeException("make_model() in impl.Root should not return null.");
      }
      this.model = this.implem_model._newComponent(new BridgeImpl_model(), false);
    }
    
    private void init_view() {
      assert this.view == null: "This is a bug.";
      assert this.implem_view == null: "This is a bug.";
      this.implem_view = this.implementation.make_view();
      if (this.implem_view == null) {
      	throw new RuntimeException("make_view() in impl.Root should not return null.");
      }
      this.view = this.implem_view._newComponent(new BridgeImpl_view(), false);
    }
    
    private void init_controller() {
      assert this.controller == null: "This is a bug.";
      assert this.implem_controller == null: "This is a bug.";
      this.implem_controller = this.implementation.make_controller();
      if (this.implem_controller == null) {
      	throw new RuntimeException("make_controller() in impl.Root should not return null.");
      }
      this.controller = this.implem_controller._newComponent(new BridgeImpl_controller(), false);
    }
    
    protected void initParts() {
      init_model();
      init_view();
      init_controller();
    }
    
    protected void init_user() {
      // nothing to do here
    }
    
    protected void initProvidedPorts() {
      init_user();
    }
    
    public ComponentImpl(final Root implem, final Root.Requires b, final boolean doInits) {
      this.bridge = b;
      this.implementation = implem;
      
      assert implem.selfComponent == null: "This is a bug.";
      implem.selfComponent = this;
      
      // prevent them to be called twice if we are in
      // a specialized component: only the last of the
      // hierarchy will call them after everything is initialised
      if (doInits) {
      	initParts();
      	initProvidedPorts();
      }
    }
    
    public IUser user() {
      return this.view().
      user()
      ;
    }
    
    private Model.Component model;
    
    private Model implem_model;
    
    private final class BridgeImpl_model implements Model.Requires {
    }
    
    public final Model.Component model() {
      return this.model;
    }
    
    private View.Component view;
    
    private View implem_view;
    
    private final class BridgeImpl_view implements View.Requires {
      public final IRead read() {
        return Root.ComponentImpl.this.model().
        read()
        ;
      }
      
      public final IWrite write() {
        return Root.ComponentImpl.this.controller().
        write()
        ;
      }
    }
    
    public final View.Component view() {
      return this.view;
    }
    
    private Controller.Component controller;
    
    private Controller implem_controller;
    
    private final class BridgeImpl_controller implements Controller.Requires {
      public final IEvent event() {
        return Root.ComponentImpl.this.view().
        event()
        ;
      }
      
      public final IUpdate update() {
        return Root.ComponentImpl.this.model().
        update()
        ;
      }
    }
    
    public final Controller.Component controller() {
      return this.controller;
    }
  }
  
  /**
   * Used to check that two components are not created from the same implementation,
   * that the component has been started to call requires(), provides() and parts()
   * and that the component is not started by hand.
   * 
   */
  private boolean init = false;;
  
  /**
   * Used to check that the component is not started by hand.
   * 
   */
  private boolean started = false;;
  
  private Root.ComponentImpl selfComponent;
  
  /**
   * Can be overridden by the implementation.
   * It will be called automatically after the component has been instantiated.
   * 
   */
  protected void start() {
    if (!this.init || this.started) {
    	throw new RuntimeException("start() should not be called by hand: to create a new component, use newComponent().");
    }
  }
  
  /**
   * This can be called by the implementation to access the provided ports.
   * 
   */
  protected Root.Provides provides() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("provides() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if provides() is needed to initialise the component.");
    }
    return this.selfComponent;
  }
  
  /**
   * This can be called by the implementation to access the required ports.
   * 
   */
  protected Root.Requires requires() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("requires() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if requires() is needed to initialise the component.");
    }
    return this.selfComponent.bridge;
  }
  
  /**
   * This can be called by the implementation to access the parts and their provided ports.
   * 
   */
  protected Root.Parts parts() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("parts() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if parts() is needed to initialise the component.");
    }
    return this.selfComponent;
  }
  
  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Model make_model();
  
  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract View make_view();
  
  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Controller make_controller();
  
  /**
   * Not meant to be used to manually instantiate components (except for testing).
   * 
   */
  public synchronized Root.Component _newComponent(final Root.Requires b, final boolean start) {
    if (this.init) {
    	throw new RuntimeException("This instance of Root has already been used to create a component, use another one.");
    }
    this.init = true;
    Root.ComponentImpl  _comp = new Root.ComponentImpl(this, b, true);
    if (start) {
    	_comp.start();
    }
    return _comp;
  }
  
  /**
   * Use to instantiate a component from this implementation.
   * 
   */
  public Root.Component newComponent() {
    return this._newComponent(new Root.Requires() {}, true);
  }
}
