﻿let logs x = printfn "%A" x
let replace a b s = (s:string).Replace((a:string), (b:string))
let parseRow row = row |> Seq.toArray |> Array.map (int << string)

let data =
    System.IO.File.ReadAllLines "data.txt"
    |> Array.map parseRow

// Part 1
let rows = Array.length data
let columns = Array.length data[0]
let indexOk row col = row >= 0 && col >= 0 && row < rows && col < columns
let getValue row col = if indexOk row col then data[row][col] else 100

let neighbors row col getter =
    let left = getter row (col-1)
    let right = getter row (col+1)
    let below = getter (row+1) col
    let above = getter (row-1) col
    [left; right; below; above]

let neighborsValue row col = neighbors row col getValue

let risk row col =
    let c = getValue row col
    if neighborsValue row col |> Seq.forall (fun x -> x > c) then c + 1
    else 0

Seq.allPairs [0..rows-1] [0..columns-1]
|> Seq.fold (fun acc (row, col) -> acc + risk row col) 0
|> logs

// Part 2
type Cell = {value: int; row: int; col: int; mutable group: (int * int) option}
let newCell value row col = {value = value; row = row; col = col; group = None}

let mutable map =
    data
    |> Array.mapi (fun ri row -> row |> Array.mapi (fun ci v -> newCell v ri ci))

let getCell row col = if indexOk row col then Some (map[row][col]) else None

let neighborsCell row col =
    neighbors row col getCell
    |> Seq.choose id

let notWall cell = cell.value <> 9

let notGrouped cell = cell.group = None

let rec visit group cell =
    if notWall cell && notGrouped cell then
        cell.group <- group
        neighborsCell cell.row cell.col
        |> Seq.iter (visit group)

Seq.allPairs [0..rows-1] [0..columns-1]
|> Seq.iter (fun (ri, ci) -> visit (Some (ri, ci)) ((getCell ri ci) |> Option.get))

map
|> Array.collect id
|> Array.filter notWall
|> Array.countBy (fun c -> c.group)
|> Array.map snd
|> Seq.sortByDescending id
|> Seq.take 3
|> Seq.fold (*) 1
|> logs