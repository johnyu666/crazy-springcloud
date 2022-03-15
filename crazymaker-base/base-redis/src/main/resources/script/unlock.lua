--- -1 failed
--- 1 success

-- unlock key
local key = KEYS[1]
local content = KEYS[2]
local value = redis.call('get', key)
if value == content then
    redis.call('del', key);
    return 1;
end
return -1