--返回值说明
--1 排队成功
--2 排队商品没有找到
--3 人数超过限制
--4 库存不足
--5 排队过了
--6 秒杀过了
-- -2 Lua 方法不存在
local function setToken(goodId, userId, token)

    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. goodId, userId);
    if oldToken then
        return 5; --5 排队过了
    end


    --获取商品缓存次数
    local goodJson = redis.call("get", "seckill:goods:" .. goodId);
    if not goodJson then
        --redis.debug("秒杀商品没有找到")
        return 2;  --2 秒杀商品没有找到
    end
    --redis.log(redis.LOG_NOTICE, goodJson)
    local goodDto = cjson.decode(goodJson);
    --redis.log(redis.LOG_NOTICE, "good title=" .. goodDto.title)
    local stockCount = tonumber(goodDto.stockCount);
    --redis.log(redis.LOG_NOTICE, "stockCount=" .. stockCount)
    if stockCount <= 0 then
        return 4;  --4 库存不足
    end

    stockCount = stockCount - 1;
    goodDto.stockCount = stockCount;

    redis.call("set", "seckill:goods:" .. goodId, cjson.encode(goodDto));
    redis.call("hset", "seckill:queue:" .. goodId, userId, token);
    return 1; --1 排队成功

end
--eg
-- /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua setToken  , 1  1  1


--返回值说明
--5 排队过了
-- -1 没有排队
local function checkToken(goodId, userId, token)
    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. goodId, userId);
    if oldToken and (token == oldToken) then
        --return 1 ;
        return 5; --5 排队过了
    end
    return -1; -- -1 没有排队
end

--eg
-- /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua checkToken  , 1  1  fca9b425-ac48-4c44-9e99-92d18898873c



local function deleteToken(goodId, userId)
    redis.call("hdel", "seckill:queue:" .. goodId, userId);
    return 1;
end
--eg
--  /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua deleteToken  , 1  1



local method = KEYS[1]

local goodId = ARGV[1]
local userId = ARGV[2]
local token = ARGV[3]

if method == 'setToken' then
    return setToken(goodId, userId, token)
elseif method == 'checkToken' then
    return checkToken(goodId, userId, token)
elseif method == 'deleteToken' then
    return deleteToken(goodId, userId)
else
    return -2; -- Lua方法不存在
end

