const URL  ="https://652d110ff9afa8ef4b26bb21.mockapi.io/theatre"

// import http from "../utils/http-client";

const list = async()=>{
    const response  = await fetch(URL,{method:"GET"})
    return await response.json()
}
const create = async (data)=>{
    const response  = await fetch(URL,{method:"POST", body: JSON.stringify(data)});
    return await response.json()
}
const update = async(id,data)=>{
    const response  = await fetch(URL+"/"+id,{method:"PUT", body: JSON.stringify(data)});
    return await response.json()
}

const dalete = async(id)=>{
    const response  = await fetch(URL+"/"+id,{method:"DELETE"});
    return await response.json()
}
const byId = async(id)=>{
    const response  = await fetch(URL+"/"+id,{method:"GET"});
    return await response.json()
}

const methods = { 
    list,
    create,
    update,
    dalete,
    byId
}
export default methods;