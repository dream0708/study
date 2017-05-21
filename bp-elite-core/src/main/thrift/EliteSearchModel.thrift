namespace java com.sohu.bp.elite.model

enum SortType {
    DESC = 1
    ASC = 2
}

struct TSearchQuestionCondition {
    1:i64       bpId            = -1;
    2:string    keywords        = "";
    3:string    tagIds          = "";
    4:string    statusArray     = "";
    5:i32       relationType    = -1;
    6:i64       relationId      = -1;
    7:i32       source          = -1;
    8:bool      titleOnly       = false;

    9:i64       minCreateTime   = -1;
    10:i64      maxCreateTime   = -1;
    11:i64      minUpdateTime   = -1;
    12:i64      maxUpdateTime   = -1;
    13:i64      minPublishTime  = -1;
    14:i64      maxPublishTime  = -1;

    15:string                sortField = "";
    16:SortType              sortType = SortType.ASC;
    17:i32                   from = 0;
    18:i32                   count = 0;
    
    19:i64 		specialId		= -1;
    20:i32		specialType		= -1;

    21:i32      minAnswerNum    = -1;
    22:i32      maxAnswerNum    = -1;

    23:i32      autoComplete    = -1;
    24:i64      banBpId         = -1;
}

struct TSearchAnswerCondition {
    1:i64       bpId        = -1;
    2:i64       questionId  = -1;
    3:string    keywords    = "";
    4:string    tagIds      = "";
    5:string    statusArray = "";
    6:i32       source      = -1;

    7:i64       minCreateTime   = -1;
    8:i64       maxCreateTime   = -1;
    9:i64       minUpdateTime   = -1;
    10:i64       maxUpdateTime   = -1;
    11:i64      minPublishTime  = -1;
    12:i64      maxPublishTime  = -1;

    13:string        sortField = "";
    14:SortType      sortType = SortType.ASC;
    15:i32           from = 0;
    16:i32           count = 0;
    
    17:i64      specialId       = -1;
    18:i32      specialType     = -1;

}

struct TSearchUserCondition {
    1:i64       bpId        = -1;
    2:i32       gender      = -1;
    3:string    keywords    = "";
    4:string    tagIds        = "";
    5:i32       status      = -1;

    6:i32       minGrade        = -1;
    7:i32       maxGrade        = -1;
    8:i64       minCreateTime   = -1;
    9:i64       maxCreateTime   = -1;
    10:i64      minUpdateTime   = -1;
    11:i64      maxUpdateTime   = -1;
    12:i64      minBirthday     = -1;
    13:i64      maxBirthday     = -1;

    14:string        sortField = "";
    15:SortType      sortType = SortType.ASC;
    16:i32           from = 0;
    17:i32           count = 0;
    
    18:i64 minFirstLoginTime = -1;
    19:i64 maxFirstLoginTime = -1;
    20:i64 minLastLoginTime = -1;
    21:i64 maxLastLoginTIme = -1;
    
 	22:string	firstLogin = "";
 	23:i32 identity = -1;

 	24:i32       minQuestionNum    = -1;
 	25:i32       maxQuestionNum    = -1;
 	26:i32       minAnswerNum      = -1;
 	27:i32       maxAnswerNum      = -1;
 	28:i32       autoComplete      = -1;
}

struct TSearchGlobalCondition {
    1:string        keywords;
    2:i64           areaCode;
    3:i32           from;
    4:i32           count;
}

struct TSearchExpertTeamCondition {
    1:i64           bpId                 = -1;
    2:i32           identity             = -1;
    3:i32           pushNum              = -1;
    4:i32           answeredNum          = -1;
    5:i32           score                = -1;
    6:i64           lastPushTime         = -1;
    7:i64           lastAnsweredTime     = -1;
    8:i64			team				 = -1;
    
    9:string        sortField            = "";
    10:SortType      sortType             = SortType.ASC;
    11:i32          from                 = 0;
    12:i32          count                = 0;
}
