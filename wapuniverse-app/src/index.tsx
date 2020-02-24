import React from 'react';
import ReactDOM from 'react-dom';

import './index.css';
import { AppUi } from "./App";
import { App } from "./editor/Editor";
import { Transaction } from 'sodiumjs';
import * as Collections from 'typescript-collections';
import FastPriorityQueue from 'fastpriorityqueue';
import FlatQueue from 'flatqueue';
import { CellSink } from './frp';


// Transaction.run(() => {
//     ReactDOM.render(<AppUi app={new App()} />, document.getElementById('root'));
// });

class Stopwatch {
    private start: number = 0;

    private loops: Array<[string, number]> = [];

    constructor() {
        this.reset();
    }

    reset() {
        this.start = Date.now();
        this.loops = [];
    }

    get(): number {
        return Date.now() - this.start;
    }

    loop(s: string) {
        this.loops.push([s, this.get()]);
    }

    dump() {
        this.loops.forEach(([s, t]) => console.log(`${s}: ${t}`));
    }
}

class Entry {
    readonly seq: number;

    constructor(seq: number) {
        this.seq = seq;
    }
}


// const prioritizedQ = new Collections.PriorityQueue<Entry>((a, b) => {
//     if (a.seq < b.seq) return 1;
//     if (a.seq > b.seq) return -1;
//     return 0;
// });

// const prioritizedQ = new FastPriorityQueue<Entry>((a, b) => {
//     return a.seq < b.seq;
// });

function mapTest(): void {
    const sink = new CellSink<number>(2);
    const mapped = Transaction.run(() => [...Array(2500).keys()].map((i) => sink.map((a) => a * i)));
    mapped.forEach((m) => m.listen(() => { }));

    const stopwatch = new Stopwatch();

    sink.send(2);

    stopwatch.loop("sink.send");
    stopwatch.dump();
}

mapTest();

function queueTest(): void {
    const prioritizedQ = new FlatQueue<Entry>();

    const entries = new Collections.Set<Entry>((a) => a.toString());


    for (let i = 0; i < 25000; ++i) {
        const e = new Entry(Math.random() * 100000);
        prioritizedQ.push(e, e.seq);
        // entries.add(e);
    }

    const stopwatch = new Stopwatch();

    let sum = 0;

    // for (let i = 0; i < 2500000; ++i) {
    //     sum += i;
    // }
    // stopwatch.loop("sum total");

    while (true) {
        const e = prioritizedQ.pop();
        if (!e) break;

        // entries.remove(e);
        sum = sum + e.seq;
    }

    stopwatch.loop("prioritizedQ + entries total");


    stopwatch.dump();
    console.log(`sum = ${sum}`);
}