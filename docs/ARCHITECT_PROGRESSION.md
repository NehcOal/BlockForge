# Architect Progression

BlockForge v5.3.0-beta.1 adds common progression models for architects.

## v5.1 Event Rewards

Settlement events and project chains can award reputation, architect
experience, unlock ids, and stability deltas through common reward/outcome
models. Item payouts remain command-driven alpha scaffolds until loader
persistence and inventories are connected.

## Items

- `blockforge_connector:architect_ledger`
- `blockforge_connector:contract_token`
- `blockforge_connector:architect_seal`

## Rewards

Contracts can award:

- reputation
- experience
- unlock feature ids
- simple item reward records
- blueprint pack reward id scaffold

The `RewardService` applies passed `ContractVerificationResult` rewards to an
`ArchitectProfile` and recalculates level/unlocks.

## Unlocks

Current alpha unlock ids:

- `advanced_wand_modes`
- `builder_station_access`
- `material_network_access`
- `hard_contracts`
- `master_contracts`
- `larger_build_limit`
- `cosmetic_blocks_planned`

Progression gating is intentionally loose in v5.0. Server operators can treat
the system as a reward/visibility layer while balancing continues.
