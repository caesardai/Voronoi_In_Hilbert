package processing.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisteredMethods
{
    int count;
    Object[] objects;
    Method[] methods;
    
    public void handle() {
        this.handle(new Object[0]);
    }
    
    public void handle(final Object[] args) {
        for (int i = 0; i < this.count; ++i) {
            try {
                this.methods[i].invoke(this.objects[i], args);
            }
            catch (Exception ex) {
                if (ex instanceof InvocationTargetException) {
                    ((InvocationTargetException)ex).getTargetException().printStackTrace();
                }
                else {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public void add(final Object o, final Method method) {
        if (this.objects == null) {
            this.objects = new Object[5];
            this.methods = new Method[5];
        }
        if (this.count == this.objects.length) {
            this.objects = (Object[])PApplet.expand(this.objects);
            this.methods = (Method[])PApplet.expand(this.methods);
        }
        this.objects[this.count] = o;
        this.methods[this.count] = method;
        ++this.count;
    }
    
    public void remove(final Object o, final Method method) {
        final int index = this.findIndex(o, method);
        if (index != -1) {
            --this.count;
            for (int i = index; i < this.count; ++i) {
                this.objects[i] = this.objects[i + 1];
                this.methods[i] = this.methods[i + 1];
            }
            this.objects[this.count] = null;
            this.methods[this.count] = null;
        }
    }
    
    protected int findIndex(final Object o, final Method obj) {
        for (int i = 0; i < this.count; ++i) {
            if (this.objects[i] == o && this.methods[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }
}