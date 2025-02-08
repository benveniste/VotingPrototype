# VotingPrototype Blockchain notes
This prototype uses two different types of "Smart Contracts" as defined for Ethereum pattern
blockchains.

### Ballot:
Each ballot contains a set of votes for a single election as an XML document.  The document
will conform to the SmofML schema defined at `http://smofs.com/smofml.xsd`.  It includes the
event name, the date of the event, the election name, an SHA-256 hash of the ballotBox contract ID, 
when the ballot was cast, a UUID associated with a member, a UUID associated with this ballot, 
the categories voted on, and the votes cast in the order of ranking.  A sample ballot is shown 
below.  Note there is nothing public linking the ballot to an individual.
<pre>&lt;?xml version='1.0' encoding='UTF-8'?&gt;
&lt;XmlBallot xmlns:xs="http://www.w3.org/2001/XMLSchema-instance" xs:noNamespaceSchemaLocation="http://smofs.com/smofml.xsd"&gt;
  &lt;event&gt;ReplacementCon&lt;/event&gt;
  &lt;eventDate&gt;2038-01-19&lt;/eventDate&gt;
  &lt;election&gt;In the Know Award Nominations&lt;/election&gt;
  &lt;electionHash&gt;9787773204&lt;/electionHash&gt;
  &lt;castAt&gt;2025-02-05T21:19:38.523118-05:00&lt;/castAt&gt;
  &lt;memberUUID&gt;9cb15cdb-3f4e-4722-852b-0c51f155cfd0&lt;/memberUUID&gt;
  &lt;ballotUUID&gt;6d578b9d-a390-49d0-bc79-198a1aee27ac&lt;/ballotUUID&gt;
  &lt;category&gt;
    &lt;name&gt;Best Novel&lt;/name&gt;
    &lt;vote&gt;Who's on First&lt;/vote&gt;
  &lt;/category&gt;
  &lt;category&gt;
    &lt;name&gt;Best Novelette&lt;/name&gt;
    &lt;vote&gt;What's on Second&lt;/vote&gt;
  &lt;/category&gt;
  &lt;category&gt;
    &lt;name&gt;Best Novella&lt;/name&gt;
    &lt;vote&gt;I don't know&lt;/vote&gt;
  &lt;/category&gt;
  &lt;category&gt;
    &lt;name&gt;Best Short Story&lt;/name&gt;
    &lt;vote&gt;I don't give a darn&lt;/vote&gt;
  &lt;/category&gt;
&lt;/XmlBallot&gt;
</pre>
### Ballot Box:
There is one ballot box per election.  It contains a list of smart contract identifiers for
all ballots cast in that election.  Should someone attempt to create a second ballot box for
an election and stuff it with previously cast votes, a mismatch of hashes in the ballots will
reveal the skullduggery.
