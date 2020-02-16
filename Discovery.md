# Discovery

## Domain Language
- `Item`: something that can be identified and bought
- `Bidder`: Person or Org that is interested in buying an `Item`
- `Bid`: Statement that a `Bidder` will pay a given price for an `Item`
- `Current price`: Current highest bid for an `Item`
- `Stop price`: The most a `Bidder` is prepared to pay for an `Item`
- `Auction`: Process for managing `Bids` for an `Item`
- `Auction House`: Institution that hosts `Auctions`

## Conventions
Use the `Item`'s identifier to refer to its `Auction`

## Requirements
- Build as a Java Swing app
- Interface will have/show:
  - For each `Item`:
    - Identifier
    - `Stop price`
    - `Current price`
    - Status
  - Button to add new `Items` for sniping.
    It shoes a dialog to allow the input of:
      - Item identifier
      - `Stop price`
- Interface will update in response to events arriving from the `Auction House`

## The Initial Plan
1. Single item - Join, lose without bidding
2. Single item - Join, bid and lose
3. Single item - Join, bid and win
4. Single item - Show price details
5. Multiple items
6. Add new items through the GUI
7. Stop bidding at stop price


