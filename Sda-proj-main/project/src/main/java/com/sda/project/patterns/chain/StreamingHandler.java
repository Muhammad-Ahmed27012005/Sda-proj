package com.sda.project.patterns.chain;

public abstract class StreamingHandler {
    protected StreamingHandler next;

    public StreamingHandler setNext(StreamingHandler next) {
        this.next = next;
        return next;
    }

    public void handle(StreamingRequest request) {
        if (next != null) {
            next.handle(request);
        }
    }
}
