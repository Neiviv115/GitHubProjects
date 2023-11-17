using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.ARFoundation;
using UnityEngine.XR.ARSubsystems;

public class ARPlaceTrackedImage : MonoBehaviour
{
    public GameObject[] ArPrefabs;
    ARSessionOrigin sessionOrigin;
    private readonly Dictionary<string, GameObject> _instantiatedPrefabs = new Dictionary<string, GameObject>();
    // Start is called before the first frame update
    private ARTrackedImageManager _aRTrackedImageManager;
    private void Awake()
    {
        // session = FindObjectOfType<ARSessionOrigin>();
        _aRTrackedImageManager = FindObjectOfType<ARTrackedImageManager>();
        sessionOrigin = GetComponent<ARSessionOrigin>();

    }
    private void OnEnable()
    {
        _aRTrackedImageManager.trackedImagesChanged += OnTrackedImagesChanged;
    }

    private void OnDisable()
    {
        _aRTrackedImageManager.trackedImagesChanged -= OnTrackedImagesChanged;
    }
    private void OnTrackedImagesChanged(ARTrackedImagesChangedEventArgs eventArgs)
    {
        // Go through all tracked images that have been added 
        // (-> new markers detected) 
        foreach (var trackedImage in eventArgs.added)
        {
            // Get the name of the reference image to search for the corresponding prefab 
            var imageName = trackedImage.referenceImage.name;

            foreach (var curPrefab in ArPrefabs)
            {
                if (string.Compare(curPrefab.name, imageName, StringComparison.Ordinal) == 0
                    && !_instantiatedPrefabs.ContainsKey(imageName))
                {
                    // Found a corresponding prefab for the reference image, and it has not been 
                    // instantiated yet > new instance, with the ARTrackedImage as parent 
                    // (so it will automatically get updated when the marker changes in real life) 

                    /* ARTrackable trackable = trackedImage.trackingState == UnityEngine.XR.ARSubsystems.TrackingState.Tracking ? trackedImage : null;
                     ARPlane plane = trackable as ARPlane;*/
                    var offset = new Vector3(0, 0, -1.5f);
                    var newPrefab = Instantiate(curPrefab,trackedImage.transform.position+offset/*Vector3.zero*/, Quaternion.identity);
                    Vector3 modelPose = new Vector3(trackedImage.transform.position.x, trackedImage.transform.position.y, trackedImage.transform.position.z);
                    newPrefab.transform.position = modelPose;
                    Vector3 rotation = new Vector3(-90, 0, 180);//set to (-90,0,180)
                    newPrefab.transform.rotation = Quaternion.Euler(rotation);
                    newPrefab.transform.parent/*.SetParent(sessionOrigin.trackablesParent, false);//*/ = trackedImage.transform;
                    

                    // Store a reference to the created prefab 
                    _instantiatedPrefabs[imageName] = newPrefab;

                }
            }
        }
        // Put this part as a comment if you want the prefab to stay
         //Disable instantiated prefabs that are no longer being actively tracked
        foreach (var trackedImage in eventArgs.updated)
        {
           _instantiatedPrefabs[trackedImage.referenceImage.name]
                .SetActive(trackedImage.trackingState == TrackingState.Tracking);
        }

        // Remove is called if the subsystem has given up looking for the trackable again.
        // (If it's invisible, its tracking state would just go to limited initially).
        // Note: ARCore doesn't seem to remove these at all; if it does, it would delete our child GameObject
        // as well.
        foreach (var trackedImage in eventArgs.removed)
        {
            // Destroy the instance in the scene.
            // Note: this code does not delete the ARTrackedImage parent, which was created
            // by AR Foundation, is managed by it and should therefore also be deleted by AR Foundation.
            Destroy(_instantiatedPrefabs[trackedImage.referenceImage.name]);
            // Also remove the instance from our array
            _instantiatedPrefabs.Remove(trackedImage.referenceImage.name);

            // Alternative: do not destroy the instance, just set it inactive
            _instantiatedPrefabs[trackedImage.referenceImage.name].SetActive(false);
        }
    }
}