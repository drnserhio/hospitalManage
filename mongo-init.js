db.createUser(
    {
        user: "root",
        pwd: "5600",
        roles: [
            {
                role: "readWrite",
                db: "chat"
            }
        ]
    }
);

db.test.insert({"name":"testCollection"})



