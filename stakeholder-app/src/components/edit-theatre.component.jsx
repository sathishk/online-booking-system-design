import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import theareService from "../services/theatre.service"
const EditTheatre = () => {

  const { id } = useParams();
  const navigate = useNavigate();
  const [theatre, setTheatre] = useState({
    title: "",
    description: "",
  });

  const { title, description } = theatre;
  const onInputChange = (e) => {
    setTheatre({ ...theatre, [e.target.name]: e.target.value });
  };

  useEffect(() => {
    loadTheatre();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    if(id){
      await theareService.update(id,theatre)
    }else{
      await theareService.create(id,theatre)
    }
    navigate("/list-theatre");
  };

  const loadTheatre = async () => {
    if(id){
      const result = await theareService.byId(id);
      setTheatre(result);
    }    
  };
  return (
    <div className="container">
      <div className="w-75 mx-auto shadow p-5">
        <h2 className="text-center mb-4">Edit A Theatre</h2>
        <form onSubmit={(e) => onSubmit(e)}>
          <div className="form-group">
            <input
              type="text"
              className="form-control form-control-lg"
              placeholder="Enter Your title"
              name="title"
              value={title}
              onChange={(e) => onInputChange(e)}
            />
          </div>
          <div className="form-group">
            <input
              type="text"
              className="form-control form-control-lg"
              placeholder="Enter Your Theatrename"
              name="description"
              value={description}
              onChange={(e) => onInputChange(e)}
            />
          </div>
          <button className="btn btn-warning btn-block">Update Theatre</button>
        </form>
      </div>
    </div>
  );
};

export default EditTheatre;
