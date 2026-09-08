High Level Design

Main Goal and what I hope to learn from this project: Create an MVP app that allows users to create polls and vote as well. In the first version, I want to focus on getting the MVP out and solving the core functionality,and users will only be able to vote as a text.

After the MVP is done then I will work on how to extend to include other functionalities

I hope to learn how to build real time systems and use AI on the frontend. I am particularly interested to see how we can cope with multiple people trying to vote at the same time and also how to reduce the latency as much as possible

MVP

Funtional Requirements
Users can create polls with a specific expiry date
Users can vote on polls that are not expired, and can only vote once per poll
Users should be able to see how many people and who have voted in real time


Non Functional Requirements
High availability, users will be able to vote at any time, the system should prioritise availability over consistency
The system should feel low latency


Interesting Edge Cases
How to deal with 2 people voting at the same time, race condition. Cannot be locks, focus should be on availability rather than consistency
How to model the polls such that it can be extended to different kind of polling methods like mcq etc
Out of Scope or will use AI
Frontend

Future Steps
Work on how to reduce the latency 
Only allow certain people to vote, some form of scoping



Core Entities
Poll
User


User can create one or more polls. User can vote on one or more polls. Current Polls only have pointcloud or MCQ.


req
body:{
    username:
    password:
}

res
body:{
    msg:"Success"
}

```POST /v1/user/login```
body:{
    username:
    password:
}

res
body:{
}

```POST /v1/poll/create```
req
body:{
    pollName:str,
    pollType:date
}

res
body:{
    pollId:uuid
}



```GET /v1/poll/getPollResults```
req
body:{
    pollId:uuid
}
res
body:{
    pollName:str,
    pollExpiry:date
    text:List[str]
}

```POST /v1/poll/vote```
req
body:{
    pollId:uuid
    text:str
}
```


DB tables
User
userID (uuid) PK
password_hash (text)
name (text)
created_at (timestamp)
updated_at (timestamp)



Poll
pollID (uuid) PK
createdBy (uuid)
expiryDate (timestamp)
status bool




PollSubmission
pollSubmissionID (uuid) PK
pollID (uuid) FK
userID (uuid) FK
text (text)
