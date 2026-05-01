# BlockForge Contracts

BlockForge v5.3.0-beta.1 introduces alpha building contracts.

## v5.1 Event And Project Sources

Contracts can now be used as standard contracts, event-generated work, project
stage requirements, or emergency repair follow-up. The source is currently
documented and represented by event/project ids in common DTOs; full loader
state binding is planned.

## Contract Board

`blockforge_connector:contract_board` is registered on NeoForge, Fabric, and Forge.
In v5.0 it is a command-driven alpha entry point.

## Built-in Templates

The common `ContractTemplates` registry includes at least 12 contract templates:

- Build a Starter Cottage
- Build a Watchtower
- Build a Stone Bridge
- Build a Storage Shed
- Build a Farm Hut
- Build a Mine Entrance
- Build a Market Stall
- Build a Small Shrine
- Build a Garden Fountain
- Build a Defensive Wall Segment
- Build a Dock
- Build a Custom Imported Blueprint

## Commands

- `/blockforge contracts list`
- `/blockforge contracts info <contractId>`
- `/blockforge contracts accept <contractId>`
- `/blockforge contracts active`
- `/blockforge contracts verify <contractId>`
- `/blockforge contracts submit <contractId>`
- `/blockforge contracts abandon <contractId>`
- `/blockforge contracts refresh`

## Verification

v5.0 verification is an alpha heuristic. It checks blueprint-level requirements:

- blueprint id or allowed tag intent
- block count range
- size range
- required block ids
- banned block ids
- simple door/window/roof/foundation heuristics
- required completion percent estimate

World snapshot verification remains loader-dependent and pending.
