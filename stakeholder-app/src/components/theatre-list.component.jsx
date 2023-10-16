import React, { useState, useEffect } from "react";
import { Navigate , NavLink} from "react-router-dom";
import theareService from "../services/theatre.service"
const TheatreList = () => {
  const [theatres, setTheatre] = useState([]);

  useEffect(() => {
    loadTheatres();
  }, []);

  const deleteTheatre = async (id) => {
    await theareService.dalete(id)
    loadTheatres();
  };

  const loadTheatres = async () => {
    const result = await theareService.list();
    console.log(result)
    setTheatre(result);
  };
  const editTheatre=(id)=>{
    Navigate(`/edit-theatre/${id}`)
  }
  return (
    <div className="home-page">
      <table className="table">
        <thead className="thead-dark">
          <tr>
            <th scope="col">#</th>
            <th scope="col">Title</th>
            <th scope="col">Description</th>
            <th scope="col">CreatedBy</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {theatres.map((theatre, index) => (
            <tr key={theatre.id}>
              <th scope="row">{index + 1}</th>
              <td>{theatre.title}</td>
              <td>{theatre.description}</td>
              <td>{theatre.createdBy}</td>
              <NavLink to={`/theatre/${theatre.id}`} end className="nav-link">View</NavLink>
              <NavLink to={`/edit-theatre/${theatre.id}`} end className="nav-link active">Edit</NavLink>
              <button
                className="btn btn-secondary"
                onClick={() => deleteTheatre(theatre.id)}
              >
                Delete
              </button>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TheatreList;
