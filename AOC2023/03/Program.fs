﻿open System

let rows =
    System.IO.File.ReadAllLines "C:\Users\peter\Repos\LearningFSharp\AOC2023\03\data"

let map =
    rows
    |> Array.map (fun row -> row |> Seq.toArray |> Array.map id)

let isSymbol symbol =
    symbol |> System.Char.IsDigit |> not
    && symbol <> '.'

type Coordinate = { x: int; y: int }
type Gear = { coordinate: Coordinate; symbol: char }
type Number =
    { value: int
      coordinates: Coordinate seq }

let isNeighbor a b =
    let xd = abs(a.x-b.x)
    let yd = abs (a.y-b.y)
    xd < 2 && yd < 2

let symbols =
    seq {
        for (y, row) in map |> Array.indexed do
            for (x, c) in row |> Array.indexed do
                if isSymbol c then
                    yield { coordinate = { x = x; y = y }; symbol = c }
    }

let numbers =
    seq {

        for (y, row) in map |> Array.indexed do

            let digits = Text.StringBuilder()
            let indices = ResizeArray<Coordinate>()

            for (x, c) in row |> Array.indexed do
                if c |> System.Char.IsDigit then
                    digits.Append c
                    indices.Add({ x = x; y = y })
                else
                    if digits.Length <> 0 then
                        yield
                            { value = digits.ToString() |> int
                              coordinates = indices }

                    indices.Clear()
                    digits.Clear()

            if digits.Length <> 0 then
                yield
                    { value = digits.ToString() |> int
                      coordinates = indices }

    }

let hasAnyCoodinateNextToSymbol number =
    number.coordinates |> Seq.exists (fun numberCoordinate ->
        symbols |> Seq.exists (fun symbol -> symbol.coordinate |> isNeighbor numberCoordinate)
    )

let part1 = numbers |> Seq.filter hasAnyCoodinateNextToSymbol |> Seq.sumBy _.value

printfn "Part 1 %i" part1

// Part 2
let gears = symbols |> Seq.filter (fun s -> s.symbol = '*')

let mutable part2 = 0
for gear in gears do
    let gCoord = gear.coordinate
    
    let mutable partNumber = ResizeArray<int>()
    let mutable countNextToGear = 0
    for number in numbers do
        
        if number.coordinates |> Seq.exists (fun numberCoord ->
            numberCoord |> isNeighbor gCoord
        ) then
            countNextToGear <- countNextToGear + 1
            partNumber.Add number.value
    
    if countNextToGear = 2 then
        let power = partNumber[0] * partNumber[1]
        part2 <- part2 + power

printfn "Part 2 %i" part2