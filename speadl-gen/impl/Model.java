package impl;

import interfaces.IRead;
import interfaces.IUpdate;

@SuppressWarnings("all")
public abstract class Model {
  public interface Requires {
  }
  
  public interface Component extends Model.Provides {
  }
  
  public interface Provides {
    /**
     * This can be called to access the provided port.
     * 
     */
    public IUpdate update();
    
    /**
     * This can be called to access the provided port.
     * 
     */
    public IRead read();
  }
  
  public interface Parts {
  }
  
  public static class ComponentImpl implements Model.Component, Model.Parts {
    private final Model.Requires bridge;
    
    private final Model implementation;
    
    public void start() {
      this.implementation.start();
      this.implementation.started = true;
    }
    
    protected void initParts() {
      
    }
    
    protected void init_update() {
      assert this.update == null: "This is a bug.";
      this.update = this.implementation.make_update();
      if (this.update == null) {
      	throw new RuntimeException("make_update() in impl.Model shall not return null.");
      }
    }
    
    protected void init_read() {
      assert this.read == null: "This is a bug.";
      this.read = this.implementation.make_read();
      if (this.read == null) {
      	throw new RuntimeException("make_read() in impl.Model shall not return null.");
      }
    }
    
    protected void initProvidedPorts() {
      init_update();
      init_read();
    }
    
    public ComponentImpl(final Model implem, final Model.Requires b, final boolean doInits) {
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
    
    private IUpdate update;
    
    public IUpdate update() {
      return this.update;
    }
    
    private IRead read;
    
    public IRead read() {
      return this.read;
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
  
  private Model.ComponentImpl selfComponent;
  
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
  protected Model.Provides provides() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("provides() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if provides() is needed to initialise the component.");
    }
    return this.selfComponent;
  }
  
  /**
   * This should be overridden by the implementation to define the provided port.
   * This will be called once during the construction of the component to initialize the port.
   * 
   */
  protected abstract IUpdate make_update();
  
  /**
   * This should be overridden by the implementation to define the provided port.
   * This will be called once during the construction of the component to initialize the port.
   * 
   */
  protected abstract IRead make_read();
  
  /**
   * This can be called by the implementation to access the required ports.
   * 
   */
  protected Model.Requires requires() {
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
  protected Model.Parts parts() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("parts() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if parts() is needed to initialise the component.");
    }
    return this.selfComponent;
  }
  
  /**
   * Not meant to be used to manually instantiate components (except for testing).
   * 
   */
  public synchronized Model.Component _newComponent(final Model.Requires b, final boolean start) {
    if (this.init) {
    	throw new RuntimeException("This instance of Model has already been used to create a component, use another one.");
    }
    this.init = true;
    Model.ComponentImpl  _comp = new Model.ComponentImpl(this, b, true);
    if (start) {
    	_comp.start();
    }
    return _comp;
  }
  
  /**
   * Use to instantiate a component from this implementation.
   * 
   */
  public Model.Component newComponent() {
    return this._newComponent(new Model.Requires() {}, true);
  }
}
