using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR;
using UnityEngine.XR.ARFoundation;



public class ImageRecognition : MonoBehaviour
{
    public GameObject prefab;
    //private ARSessionOrigin session;
    private ARTrackedImageManager _aRTrackedImageManager;
    private void Awake()
    {
       // session = FindObjectOfType<ARSessionOrigin>();
        _aRTrackedImageManager = FindObjectOfType<ARTrackedImageManager>();

    }
    private void OnEnable()
    {
        _aRTrackedImageManager.trackedImagesChanged += OnImageChanged;
    }

    private void OnDisable()
    {
        _aRTrackedImageManager.trackedImagesChanged -= OnImageChanged;
    }
   public void OnImageChanged(ARTrackedImagesChangedEventArgs args)
    {
        foreach(var trackedImage in args.added)
        {
            /* Debug.Log(trackedImage.name);
             Vector3 markerPosition = trackedImage.transform.position;
             ARTrackable trackable = trackedImage.trackingState == UnityEngine.XR.ARSubsystems.TrackingState.Tracking ? trackedImage : null;
             ARPlane plane = trackable as ARPlane;

             GameObject prefabInstance = Instantiate(prefab, Vector3.zero, Quaternion.identity);
             Vector3 modelPose = new Vector3(trackedImage.transform.position.x, plane.center.y, trackedImage.transform.position.z);
             prefabInstance.transform.position = modelPose;
             prefabInstance.transform.parent =trackedImage.transform;

 */
            // Go through all tracked images that have been added 
            // (-> new markers detected) 
            
                // Get the name of the reference image to search for the corresponding prefab 
                /*var imageName = trackedImage.referenceImage.name;

                foreach (var curPrefab in ArPrefabs)
                {
                    if (string.Compare(curPrefab.name, imageName, StringComparison.Ordinal) == 0
                        && !_instantiatedPrefabs.ContainsKey(imageName))
                    {
                        // Found a corresponding prefab for the reference image, and it has not been 
                        // instantiated yet > new instance, with the ARTrackedImage as parent 
                        // (so it will automatically get updated when the marker changes in real life) 
                        var newPrefab = Instantiate(curPrefab, trackedImage.transform);
                        // Store a reference to the created prefab 
                        _instantiatedPrefabs[imageName] = newPrefab;
                    }
                }*/
            
        }
    }
}
