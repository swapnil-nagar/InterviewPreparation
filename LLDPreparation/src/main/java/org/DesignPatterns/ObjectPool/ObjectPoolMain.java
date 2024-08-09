package org.DesignPatterns.ObjectPool;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.ArrayList;
import java.util.List;

// Define an interface for pooled objects
interface Poolable {
    void reset();  // Method to reset the object state
}

// Implement a generic ObjectPool class
class ObjectPool<T extends Poolable> {
    private final List<T> availableObjects = new ArrayList<>();
    private final List<T> inUseObjects = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final int maxSize;
    private final ObjectFactory<T> factory;

    // Constructor accepts a factory to create new instances of T
    public ObjectPool(int maxSize, ObjectFactory<T> factory) {
        this.maxSize = maxSize;
        this.factory = factory;
    }

    // Borrow an object from the pool
    public T borrowObject() throws InterruptedException {
        lock.lock();
        try {
            while (availableObjects.isEmpty() && inUseObjects.size() == maxSize) {
                System.out.println("Waiting for pooled object.");
                condition.await();
            }

            T obj;
            if (!availableObjects.isEmpty()) {
                obj = availableObjects.remove(availableObjects.size() - 1);
            } else {
                obj = factory.createObject();
            }
            inUseObjects.add(obj);
            return obj;
        } finally {
            lock.unlock();
        }
    }

    // Return an object to the pool
    public void returnObject(T obj) {
        lock.lock();
        try {
            obj.reset();
            inUseObjects.remove(obj);
            availableObjects.add(obj);
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}

// Define an interface for a factory that creates objects
interface ObjectFactory<T> {
    T createObject();
}

// Implement a Poolable class example
class PooledObject implements Poolable {
    // Simulate a resource-intensive object
    public void doSomething() {
        System.out.println("Using pooled object.");
    }

    // Reset the object state (required by the Poolable interface)
    @Override
    public void reset() {
        // Reset the object state
        System.out.println("Resetting pooled object.");
    }
}

// Implement a factory for PooledObject
class PooledObjectFactory implements ObjectFactory<PooledObject> {
    @Override
    public PooledObject createObject() {
        return new PooledObject();
    }
}

public class ObjectPoolMain {
    public static void main(String[] args) {
        ObjectPool<PooledObject> pool = new ObjectPool<>(3, new PooledObjectFactory());

        Runnable task = () -> {
            try {
                PooledObject obj = pool.borrowObject();
                obj.doSomething();
                Thread.sleep(2000);  // Simulate work with the object
                pool.returnObject(obj);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < 5; i++) {
            new Thread(task).start();
        }
    }
}