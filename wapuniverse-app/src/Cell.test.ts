import {CellSink} from "sodium";
import {expect} from 'chai';

it('Cell.map', () => {
  const inputs: number[] = [];
  const outputs: string[] = [];

  const source = new CellSink(1);

  expect(source.getVertex__().refCount()).to.be.equal(0);

  const mapped = source.map((i) => {
    inputs.push(i);
    return `s${i}`;
  });

  expect(inputs).to.be.deep.equal([1]);
  expect(mapped.sample()).to.be.equal("s1");
  expect(source.getVertex__().refCount()).to.be.equal(0);
  expect(mapped.getVertex__().refCount()).to.be.equal(0);

  const unsubscribe = mapped.listen((value) => {
    outputs.push(value);
  });

  expect(inputs).to.be.deep.equal([1]);
  expect(outputs).to.be.deep.equal([]);
  expect(mapped.sample()).to.be.equal("s1");
  expect(source.getVertex__().refCount()).to.be.equal(1);
  expect(mapped.getVertex__().refCount()).to.be.equal(1);

  source.send(2);

  expect(inputs).to.be.deep.equal([1, 2]);
  expect(outputs).to.be.deep.equal(["s2"]);
  expect(mapped.sample()).to.be.equal("s2");

  source.send(3);

  expect(inputs).to.be.deep.equal([1, 2, 3]);
  expect(outputs).to.be.deep.equal(["s2", "s3"]);
  expect(mapped.sample()).to.be.equal("s3");

  source.send(3);

  expect(inputs).to.be.deep.equal([1, 2, 3]);
  expect(outputs).to.be.deep.equal(["s2", "s3"]);
  expect(mapped.sample()).to.be.equal("s3");

  unsubscribe();

  expect(inputs).to.be.deep.equal([1, 2, 3]);
  expect(outputs).to.be.deep.equal(["s2", "s3"]);
  expect(mapped.sample()).to.be.equal("s3");
  expect(source.getVertex__().refCount()).to.be.equal(0);
  expect(mapped.getVertex__().refCount()).to.be.equal(0);
});

it('Cell.flatMap', () => {
  const inputs: number[] = [];
  const outputs: string[] = [];

  const nested1 = new CellSink("1,1");
  const nested2 = new CellSink("2,1");
  const nested3 = new CellSink("3,1");
  const queue = [nested1, nested2, nested3];

  const source = new CellSink(1);

  expect(source.getVertex__().refCount()).to.be.equal(0);
  expect(nested1.getVertex__().refCount()).to.be.equal(0);
  expect(nested2.getVertex__().refCount()).to.be.equal(0);
  expect(nested3.getVertex__().refCount()).to.be.equal(0);

  const flatMapped = source.flatMap((i) => {
    inputs.push(i);
    return queue.shift()!;
  });

  expect(inputs).to.be.deep.equal([1]);
  expect(flatMapped.sample()).to.be.equal("1,1");
  expect(source.getVertex__().refCount()).to.be.equal(0);
  expect(nested1.getVertex__().refCount()).to.be.equal(0);
  expect(flatMapped.getVertex__().refCount()).to.be.equal(0);

  const unsubscribe = flatMapped.listen((value) => {
    outputs.push(value);
  });

  expect(inputs).to.be.deep.equal([1]);
  expect(outputs).to.be.deep.equal([]);
  expect(flatMapped.sample()).to.be.equal("1,1");
  expect(source.getVertex__().refCount()).to.be.equal(1);
  expect(nested1.getVertex__().refCount()).to.be.equal(1);
  expect(flatMapped.getVertex__().refCount()).to.be.equal(1);

  nested1.send("1,2");

  expect(inputs).to.be.deep.equal([1]);
  expect(outputs).to.be.deep.equal(["1,2"]);
  expect(flatMapped.sample()).to.be.equal("1,2");

  nested1.send("1,3");

  expect(inputs).to.be.deep.equal([1]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3"]);
  expect(flatMapped.sample()).to.be.equal("1,3");

  source.send(2);

  expect(inputs).to.be.deep.equal([1, 2]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3", "2,1"]);
  expect(flatMapped.sample()).to.be.equal("2,1");
  expect(source.getVertex__().refCount()).to.be.equal(1);
  expect(nested1.getVertex__().refCount()).to.be.equal(0);
  expect(nested2.getVertex__().refCount()).to.be.equal(1);

  nested2.send("2,2");

  expect(inputs).to.be.deep.equal([1, 2]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3", "2,1", "2,2"]);
  expect(flatMapped.sample()).to.be.equal("2,2");

  nested1.send("1,4"); // should be ignored

  expect(inputs).to.be.deep.equal([1, 2]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3", "2,1", "2,2"]);
  expect(flatMapped.sample()).to.be.equal("2,2");

  source.send(3);

  expect(inputs).to.be.deep.equal([1, 2, 3]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3", "2,1", "2,2", "3,1"]);
  expect(flatMapped.sample()).to.be.equal("3,1");

  unsubscribe();

  expect(inputs).to.be.deep.equal([1, 2, 3]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3", "2,1", "2,2", "3,1"]);
  expect(flatMapped.sample()).to.be.equal("3,1");
  expect(source.getVertex__().refCount()).to.be.equal(0);
  expect(nested1.getVertex__().refCount()).to.be.equal(0);
  expect(nested2.getVertex__().refCount()).to.be.equal(0);
  expect(nested3.getVertex__().refCount()).to.be.equal(0);
  expect(flatMapped.getVertex__().refCount()).to.be.equal(0);
});

it('Cell.flatMap (same nested returned twice)', () => {
  const inputs: number[] = [];
  const outputs: string[] = [];

  const nested = new CellSink("1,1");
  const source = new CellSink(1);

  const flatMapped = source.flatMap((i) => {
    inputs.push(i);
    return nested;
  });

  flatMapped.listen((value) => {
    outputs.push(value);
  });

  nested.send("1,2");

  source.send(2);

  nested.send("1,3");

  expect(inputs).to.be.deep.equal([1, 2]);
  expect(outputs).to.be.deep.equal(["1,2", "1,3"]);
  expect(flatMapped.sample()).to.be.equal("1,3");
});

it('Cell.lift', () => {
  const inputs: [number, string][] = [];
  const outputs: string[] = [];

  const source1 = new CellSink(1);
  const source2 = new CellSink("a");

  const lifted = source1.lift(source2, (n, s) => {
    inputs.push([n, s]);
    return `${n}${s}`;
  });

  expect(inputs).to.be.deep.equal([[1, "a"]]);
  expect(lifted.sample()).to.be.equal("1a");
  expect(source1.getVertex__().refCount()).to.be.equal(0);
  expect(source2.getVertex__().refCount()).to.be.equal(0);
  expect(lifted.getVertex__().refCount()).to.be.equal(0);

  const unsubscribe = lifted.listen((value) => {
    outputs.push(value);
  });

  expect(inputs).to.be.deep.equal([[1, "a"]]);
  expect(outputs).to.be.deep.equal([]);
  expect(lifted.sample()).to.be.equal("1a");
  expect(source1.getVertex__().refCount()).to.be.equal(1);
  expect(source2.getVertex__().refCount()).to.be.equal(1);
  expect(lifted.getVertex__().refCount()).to.be.equal(1);

  source1.send(2);

  expect(inputs).to.be.deep.equal([[1, "a"], [2, "a"]]);
  expect(outputs).to.be.deep.equal(["2a"]);
  expect(lifted.sample()).to.be.equal("2a");

  source2.send("b");

  expect(inputs).to.be.deep.equal([[1, "a"], [2, "a"], [2, "b"]]);
  expect(outputs).to.be.deep.equal(["2a", "2b"]);
  expect(lifted.sample()).to.be.equal("2b");

  unsubscribe();

  expect(inputs).to.be.deep.equal([[1, "a"], [2, "a"], [2, "b"]]);
  expect(outputs).to.be.deep.equal(["2a", "2b"]);
  expect(lifted.sample()).to.be.equal("2b");
  expect(source1.getVertex__().refCount()).to.be.equal(0);
  expect(source2.getVertex__().refCount()).to.be.equal(0);
  expect(lifted.getVertex__().refCount()).to.be.equal(0);
});


